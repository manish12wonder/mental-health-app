package com.manish.mindora.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
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
) {
    private val disclaimerKey = booleanPreferencesKey("disclaimer_accepted")

    val disclaimerAccepted: Flow<Boolean> = context.userPrefsDataStore.data.map { prefs ->
        prefs[disclaimerKey] ?: false
    }

    suspend fun setDisclaimerAccepted(value: Boolean) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[disclaimerKey] = value
        }
    }
}
