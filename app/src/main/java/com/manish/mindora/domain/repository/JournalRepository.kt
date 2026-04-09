package com.manish.mindora.domain.repository

import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun observeEntries(): Flow<List<JournalEntry>>

    suspend fun saveEntryWithAnalysis(
        text: String,
    ): Result<JournalEntry>
    suspend fun saveQuickMood(mood: Mood): Result<JournalEntry>
}
