package com.manish.mindora.fakes

import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeJournalRepository(
    initial: List<JournalEntry> = emptyList(),
) : JournalRepository {

    private val _entries = MutableStateFlow(initial)

    override fun observeEntries(): Flow<List<JournalEntry>> = _entries.asStateFlow()

    override suspend fun saveEntryWithAnalysis(text: String): Result<JournalEntry> {
        val entry = JournalEntry(
            id = "fake-${_entries.value.size + 1}",
            userId = "fake-user",
            text = text,
            createdAtEpochMillis = System.currentTimeMillis(),
            mood = Mood.Neutral,
            confidence = 0.5f,
            feedback = "Test feedback",
        )
        _entries.value = listOf(entry) + _entries.value
        return Result.success(entry)
    }

    override suspend fun saveQuickMood(mood: Mood): Result<JournalEntry> {
        val entry = JournalEntry(
            id = "fake-quick-${_entries.value.size + 1}",
            userId = "fake-user",
            text = "Quick mood check-in",
            createdAtEpochMillis = System.currentTimeMillis(),
            mood = mood,
            confidence = 1f,
            feedback = "Quick feedback",
        )
        _entries.value = listOf(entry) + _entries.value
        return Result.success(entry)
    }

    fun replaceAll(entries: List<JournalEntry>) {
        _entries.value = entries
    }
}
