package com.manish.mindora.data.local.db

import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood

/** Single-device MVP: stable placeholder for domain model compatibility. */
internal const val LOCAL_DEVICE_USER_ID = "local"

fun JournalEntryEntity.toDomain(): JournalEntry {
    val mood = runCatching { Mood.valueOf(moodName) }.getOrElse { Mood.Neutral }
    return JournalEntry(
        id = id,
        userId = LOCAL_DEVICE_USER_ID,
        text = text,
        createdAtEpochMillis = createdAtEpochMillis,
        mood = mood,
        confidence = confidence,
        feedback = feedback,
    )
}
