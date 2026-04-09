package com.manish.mindora.presentation.insights

import com.manish.mindora.domain.model.Mood

data class DailyMoodSlot(
    val dayStartMillis: Long,
    val mood: Mood?,
    val confidence: Float?,
)
