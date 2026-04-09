package com.manish.mindora.domain.feedback

import com.manish.mindora.domain.model.Mood

fun interface FeedbackMessageProvider {
    fun messageFor(mood: Mood): String
}
