package com.manish.mindora.domain.model

data class JournalEntry(
    val id: String,
    val userId: String,
    val text: String,
    val createdAtEpochMillis: Long,
    val mood: Mood,
    val confidence: Float,
    val feedback: String,
)
