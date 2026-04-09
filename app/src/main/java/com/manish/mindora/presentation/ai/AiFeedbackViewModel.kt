package com.manish.mindora.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AiFeedbackUiState(
    val latestEntry: JournalEntry? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class AiFeedbackViewModel @Inject constructor(
    journalRepository: JournalRepository,
) : ViewModel() {

    val uiState: StateFlow<AiFeedbackUiState> = journalRepository.observeEntries()
        .map { entries ->
            AiFeedbackUiState(
                latestEntry = entries.firstOrNull(),
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AiFeedbackUiState(isLoading = true),
        )
}
