package com.manish.mindora.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.manish.mindora.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mindora_user_prefs",
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserLocalPreferences {

    private val disclaimerKey = booleanPreferencesKey("disclaimer_accepted")
    private val displayNameKey = stringPreferencesKey("user_display_name")
    private val themeModeKey = stringPreferencesKey("theme_mode")

    override val disclaimerAccepted: Flow<Boolean> = context.userPrefsDataStore.data.map { prefs ->
        prefs[disclaimerKey] ?: false
    }

    override suspend fun setDisclaimerAccepted(value: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[disclaimerKey] = value
        }
    }

    override val displayName: Flow<String> = context.userPrefsDataStore.data.map { prefs ->
        prefs[displayNameKey].orEmpty()
    }

    override suspend fun setDisplayName(name: String) {
        val trimmed = name.trim()
        context.userPrefsDataStore.edit { prefs ->
            prefs[displayNameKey] = trimmed
        }
    }

    override val themeMode: Flow<ThemeMode> = context.userPrefsDataStore.data.map { prefs ->
        ThemeMode.fromPreferenceString(prefs[themeModeKey])
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[themeModeKey] = mode.toPreferenceString()
        }
    }
}
