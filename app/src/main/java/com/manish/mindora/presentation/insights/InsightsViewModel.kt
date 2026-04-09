package com.manish.mindora.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class InsightsUiState(
    val weeklySlots: List<DailyMoodSlot> = emptyList(),
    val recentEntries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val daysWithMoodThisWeek: Int = 0,
    val entriesThisWeek: Int = 0,
    val weekRangeLabel: String = "",
    val topMood: Mood? = null,
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    journalRepository: JournalRepository,
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = journalRepository.observeEntries()
        .map { entries ->
            buildInsightsUiState(entries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InsightsUiState(isLoading = true),
        )
}

private fun buildInsightsUiState(entries: List<JournalEntry>): InsightsUiState {
    val slots = MoodHistoryAggregator.lastEntryPerDay(entries)
    val daysWithMoodThisWeek = slots.count { it.mood != null }
    val entriesThisWeek = if (slots.isEmpty()) {
        0
    } else {
        val from = slots.minOf { it.dayStartMillis }
        val toExclusive = slots.maxOf { it.dayStartMillis } + DAY_MS
        entries.count { it.createdAtEpochMillis in from until toExclusive }
    }
    val topMood = slots
        .mapNotNull { it.mood }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
    val df = SimpleDateFormat("MMM d", Locale.getDefault())
    val weekRangeLabel = if (slots.isNotEmpty()) {
        val a = df.format(Date(slots.first().dayStartMillis))
        val b = df.format(Date(slots.last().dayStartMillis))
        "$a – $b"
    } else {
        ""
    }
    return InsightsUiState(
        weeklySlots = slots,
        recentEntries = entries.take(30),
        isLoading = false,
        daysWithMoodThisWeek = daysWithMoodThisWeek,
        entriesThisWeek = entriesThisWeek,
        weekRangeLabel = weekRangeLabel,
        topMood = topMood,
    )
}

private const val DAY_MS = 24L * 60 * 60 * 1000
