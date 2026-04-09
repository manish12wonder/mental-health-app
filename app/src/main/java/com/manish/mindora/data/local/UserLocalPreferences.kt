package com.manish.mindora.data.local

import com.manish.mindora.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserLocalPreferences {
    val disclaimerAccepted: Flow<Boolean>
    suspend fun setDisclaimerAccepted(value: Boolean)

    val displayName: Flow<String>
    suspend fun setDisplayName(name: String)

    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
