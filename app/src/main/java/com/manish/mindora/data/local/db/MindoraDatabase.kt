package com.manish.mindora.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

internal const val MINDORA_DB_VERSION = 1

@Database(
    entities = [JournalEntryEntity::class],
    version = MINDORA_DB_VERSION,
    exportSchema = true,
)
abstract class MindoraDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao
}
