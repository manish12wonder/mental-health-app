package com.manish.mindora.data.repository

import com.manish.mindora.data.local.db.JournalEntryDao
import com.manish.mindora.data.local.db.JournalEntryEntity
import com.manish.mindora.data.local.db.toDomain
import com.manish.mindora.domain.feedback.FeedbackMessageProvider
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.domain.repository.JournalRepository
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val dao: JournalEntryDao,
    private val sentimentAnalyzer: SentimentAnalyzer,
    private val feedbackMessageProvider: FeedbackMessageProvider,
) : JournalRepository {

    override fun observeEntries(): Flow<List<JournalEntry>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun saveEntryWithAnalysis(text: String): Result<JournalEntry> = runCatching {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) error("Empty journal text")

        val analysis = sentimentAnalyzer.analyze(trimmed)
        val feedback = feedbackMessageProvider.messageFor(analysis.mood)
        val entity = JournalEntryEntity(
            id = UUID.randomUUID().toString(),
            text = trimmed,
            createdAtEpochMillis = System.currentTimeMillis(),
            moodName = analysis.mood.name,
            confidence = analysis.confidence,
            feedback = feedback,
            rawLabel = analysis.rawLabel.orEmpty(),
        )
        dao.insert(entity)
        entity.toDomain()
    }

    override suspend fun saveQuickMood(mood: Mood): Result<JournalEntry> = runCatching {
        val feedback = feedbackMessageProvider.messageFor(mood)
        val entity = JournalEntryEntity(
            id = UUID.randomUUID().toString(),
            text = QUICK_MOOD_TEXT,
            createdAtEpochMillis = System.currentTimeMillis(),
            moodName = mood.name,
            confidence = 1f,
            feedback = feedback,
            rawLabel = "quick_pick",
        )
        dao.insert(entity)
        entity.toDomain()
    }

    companion object {
        private const val QUICK_MOOD_TEXT = "Quick mood check-in"
    }
}
