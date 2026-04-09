package com.manish.mindora.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [Index(value = ["createdAtEpochMillis"])],
)
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val text: String,
    val createdAtEpochMillis: Long,
    val moodName: String,
    val confidence: Float,
    val feedback: String,
    val rawLabel: String,
)
