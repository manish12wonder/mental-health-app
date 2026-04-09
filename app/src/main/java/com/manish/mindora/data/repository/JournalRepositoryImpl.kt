package com.manish.mindora.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.manish.mindora.core.FirestorePaths
import com.manish.mindora.domain.feedback.FeedbackMessageProvider
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.domain.repository.JournalRepository
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sentimentAnalyzer: SentimentAnalyzer,
    private val feedbackMessageProvider: FeedbackMessageProvider,
) : JournalRepository {

    override fun observeEntries(): Flow<List<JournalEntry>> = callbackFlow {
        var firestoreRegistration: ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            firestoreRegistration?.remove()
            firestoreRegistration = null
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                trySend(emptyList())
            } else {
                firestoreRegistration = entriesCollection(uid)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(200)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { it.toJournalEntry() }.orEmpty()
                        trySend(list)
                    }
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose {
            auth.removeAuthStateListener(authListener)
            firestoreRegistration?.remove()
        }
    }

    override suspend fun saveEntryWithAnalysis(text: String): Result<JournalEntry> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val trimmed = text.trim()
        if (trimmed.isEmpty()) error("Empty journal text")

        val analysis = sentimentAnalyzer.analyze(trimmed)
        val feedback = feedbackMessageProvider.messageFor(analysis.mood)

        val doc = entriesCollection(uid).document()
        val data = hashMapOf(
            "userId" to uid,
            "text" to trimmed,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "mood" to analysis.mood.name,
            "confidence" to analysis.confidence.toDouble(),
            "feedback" to feedback,
            "rawLabel" to (analysis.rawLabel ?: ""),
        )
        doc.set(data).await()
        val snap = doc.get().await()
        snap.toJournalEntry() ?: error("Failed to read saved entry")
    }

    override suspend fun saveQuickMood(mood: Mood): Result<JournalEntry> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val feedback = feedbackMessageProvider.messageFor(mood)
        val doc = entriesCollection(uid).document()
        val data = hashMapOf(
            "userId" to uid,
            "text" to QUICK_MOOD_TEXT,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "mood" to mood.name,
            "confidence" to 1.0,
            "feedback" to feedback,
            "rawLabel" to "quick_pick",
        )
        doc.set(data).await()
        val snap = doc.get().await()
        snap.toJournalEntry() ?: error("Failed to read saved entry")
    }

    private fun entriesCollection(uid: String) = firestore
        .collection(FirestorePaths.USERS)
        .document(uid)
        .collection(FirestorePaths.ENTRIES)

    companion object {
        private const val QUICK_MOOD_TEXT = "Quick mood check-in"
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toJournalEntry(): JournalEntry? {
    val id = id
    val uid = getString("userId") ?: return null
    val text = getString("text") ?: return null
    val moodStr = getString("mood") ?: return null
    val mood = runCatching { Mood.valueOf(moodStr) }.getOrNull() ?: Mood.Neutral
    val confidence = (getDouble("confidence") ?: 0.0).toFloat()
    val feedback = getString("feedback") ?: ""
    val millis = when (val ts = getTimestamp("createdAt")) {
        null -> System.currentTimeMillis()
        else -> ts.toDate().time
    }
    return JournalEntry(
        id = id,
        userId = uid,
        text = text,
        createdAtEpochMillis = millis,
        mood = mood,
        confidence = confidence,
        feedback = feedback,
    )
}
