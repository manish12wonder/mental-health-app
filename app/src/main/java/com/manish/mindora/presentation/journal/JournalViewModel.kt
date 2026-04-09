package com.manish.mindora.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.domain.repository.JournalRepository
import com.manish.mindora.presentation.util.countWords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface JournalSaveUiState {
    data object Idle : JournalSaveUiState
    data object Saving : JournalSaveUiState
    data class Success(val feedback: String) : JournalSaveUiState
    data class Error(val message: String) : JournalSaveUiState
    data class WordLimitExceeded(val maxWords: Int) : JournalSaveUiState
}

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
) : ViewModel() {

    private val _saveUiState = MutableStateFlow<JournalSaveUiState>(JournalSaveUiState.Idle)
    val saveUiState: StateFlow<JournalSaveUiState> = _saveUiState.asStateFlow()

    fun saveEntry(text: String, selectedTags: Set<String>) {
        if (_saveUiState.value is JournalSaveUiState.Saving) return
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val words = countWords(trimmed)
        if (words > MAX_WORDS) {
            _saveUiState.value = JournalSaveUiState.WordLimitExceeded(MAX_WORDS)
            return
        }

        viewModelScope.launch {
            _saveUiState.value = JournalSaveUiState.Saving
            val body = buildString {
                append(trimmed)
                if (selectedTags.isNotEmpty()) {
                    append("\n\n")
                    append(selectedTags.joinToString(" ") { "#$it" })
                }
            }
            val result = journalRepository.saveEntryWithAnalysis(body)
            _saveUiState.value = result.fold(
                onSuccess = { JournalSaveUiState.Success(it.feedback) },
                onFailure = { e ->
                    JournalSaveUiState.Error(
                        e.message ?: "Could not save your entry.",
                    )
                },
            )
        }
    }

    fun consumeSaveUiState() {
        _saveUiState.value = JournalSaveUiState.Idle
    }

    companion object {
        const val MAX_WORDS = 500
    }
}
