package com.manish.mindora.domain.model

data class SentimentAnalysis(
    val mood: Mood,
    val confidence: Float,
    val rawLabel: String?,
)
