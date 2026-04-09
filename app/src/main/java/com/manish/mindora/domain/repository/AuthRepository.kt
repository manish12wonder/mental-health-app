package com.manish.mindora.domain.repository

interface AuthRepository {
    suspend fun ensureSignedIn(): Result<Unit>

    val currentUserId: String?
}
