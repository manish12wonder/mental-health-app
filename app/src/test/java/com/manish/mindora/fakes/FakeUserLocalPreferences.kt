package com.manish.mindora.fakes

import com.manish.mindora.data.local.UserLocalPreferences
import com.manish.mindora.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserLocalPreferences(
    disclaimerAccepted: Boolean = true,
    displayName: String = "TestUser",
    themeMode: ThemeMode = ThemeMode.SYSTEM,
) : UserLocalPreferences {

    private val _disclaimer = MutableStateFlow(disclaimerAccepted)
    private val _displayName = MutableStateFlow(displayName)
    private val _themeMode = MutableStateFlow(themeMode)

    override val disclaimerAccepted: Flow<Boolean> = _disclaimer

    override suspend fun setDisclaimerAccepted(value: Boolean) {
        _disclaimer.value = value
    }

    override val displayName: Flow<String> = _displayName

    override suspend fun setDisplayName(name: String) {
        _displayName.value = name.trim()
    }

    override val themeMode: Flow<ThemeMode> = _themeMode

    override suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
