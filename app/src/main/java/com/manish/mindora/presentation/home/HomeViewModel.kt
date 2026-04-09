package com.manish.mindora.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.R
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val greetingResId: Int = R.string.greeting_default,
    val todayEntry: JournalEntry? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = journalRepository.observeEntries()
        .map { entries ->
            HomeUiState(
                greetingResId = greetingForHour(),
                todayEntry = entries.latestToday(),
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState(isLoading = true),
        )

    private val _quickMoodSaving = MutableStateFlow<Mood?>(null)
    val quickMoodSaving: StateFlow<Mood?> = _quickMoodSaving.asStateFlow()

    fun saveQuickMood(mood: Mood) {
        if (_quickMoodSaving.value != null) return
        viewModelScope.launch {
            _quickMoodSaving.value = mood
            try {
                journalRepository.saveQuickMood(mood)
            } finally {
                _quickMoodSaving.value = null
            }
        }
    }

    private fun greetingForHour(): Int = when (Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
        in 5..11 -> R.string.greeting_morning
        in 12..16 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }
}

private fun List<JournalEntry>.latestToday(locale: Locale = Locale.getDefault()): JournalEntry? {
    val cal = Calendar.getInstance(locale)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.DAY_OF_MONTH, 1)
    val end = cal.timeInMillis
    return filter { it.createdAtEpochMillis in start until end }
        .maxByOrNull { it.createdAtEpochMillis }
}
