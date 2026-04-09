package com.manish.mindora.testutil

import android.content.Context
import java.io.File

/**
 * Clears DataStore user prefs (disclaimer, name, theme) so onboarding runs again.
 * Room is not deleted here: the Hilt singleton database can keep a stale handle after
 * [Context.deleteDatabase] in the same process; nav/smoke tests do not require an empty DB.
 */
fun clearMindoraLocalStorage(context: Context) {
    val dataStoreDir = File(context.filesDir, "datastore")
    if (dataStoreDir.isDirectory) {
        dataStoreDir.listFiles()?.forEach { it.delete() }
    }
}
