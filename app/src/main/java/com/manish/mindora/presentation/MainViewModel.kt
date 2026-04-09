package com.manish.mindora.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.mindora.data.local.UserLocalPreferences
import com.manish.mindora.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserLocalPreferences,
) : ViewModel() {

    val disclaimerAccepted: StateFlow<Boolean> = userPreferences.disclaimerAccepted
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val displayName: StateFlow<String> = userPreferences.displayName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    fun acknowledgeDisclaimer() {
        viewModelScope.launch {
            userPreferences.setDisclaimerAccepted(true)
        }
    }

    fun saveDisplayName(name: String) {
        viewModelScope.launch {
            userPreferences.setDisplayName(name)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }
}
