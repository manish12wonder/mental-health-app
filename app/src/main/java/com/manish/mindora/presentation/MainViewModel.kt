package com.manish.mindora.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.data.local.UserPreferencesRepository
import com.manish.mindora.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val disclaimerAccepted: StateFlow<Boolean> = userPreferencesRepository.disclaimerAccepted
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            authRepository.ensureSignedIn()
        }
    }

    fun acknowledgeDisclaimer() {
        viewModelScope.launch {
            userPreferencesRepository.setDisclaimerAccepted(true)
        }
    }
}
