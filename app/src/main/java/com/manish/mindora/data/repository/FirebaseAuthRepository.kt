package com.manish.mindora.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.manish.mindora.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun ensureSignedIn(): Result<Unit> = runCatching {
        if (auth.currentUser != null) return@runCatching
        auth.signInAnonymously().await()
    }
}
