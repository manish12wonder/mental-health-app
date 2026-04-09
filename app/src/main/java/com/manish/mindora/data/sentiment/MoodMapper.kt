package com.manish.mindora.data.sentiment

import com.manish.mindora.domain.model.Mood
import javax.inject.Inject
import javax.inject.Singleton

private const val CONFIDENCE_FLOOR = 0.35f

/**
 * Maps external model labels (e.g. Hugging Face cardiffnlp/twitter-roberta-base-emotion)
 * to app [Mood]. Falls back to [Mood.Neutral] when below [CONFIDENCE_FLOOR].
 */
@Singleton
class MoodMapper @Inject constructor() {

    fun fromHuggingFaceLabel(label: String, score: Float): Pair<Mood, Float> {
        val mood = when (label.lowercase()) {
            "joy", "love", "optimism", "surprise" -> Mood.Happy
            "sadness", "fear" -> Mood.Sad
            "anger", "annoyance", "disapproval" -> Mood.Angry
            else -> Mood.Neutral
        }
        val confidence = score.coerceIn(0f, 1f)
        return if (confidence < CONFIDENCE_FLOOR) {
            Mood.Neutral to confidence
        } else {
            mood to confidence
        }
    }

    fun fromKeywordHeuristic(text: String): Pair<Mood, Float> {
        val lower = text.lowercase()
        val happyHits = listOf("happy", "great", "good", "grateful", "love", "excited", "joy")
            .count { lower.contains(it) }
        val sadHits = listOf("sad", "down", "lonely", "cry", "hurt", "depressed", "empty")
            .count { lower.contains(it) }
        val angryHits = listOf("angry", "mad", "furious", "hate", "annoyed", "frustrated")
            .count { lower.contains(it) }

        return when {
            angryHits >= sadHits && angryHits >= happyHits && angryHits > 0 ->
                Mood.Angry to (0.5f + 0.1f * angryHits).coerceAtMost(0.95f)
            sadHits >= happyHits && sadHits > 0 ->
                Mood.Sad to (0.5f + 0.1f * sadHits).coerceAtMost(0.95f)
            happyHits > 0 ->
                Mood.Happy to (0.5f + 0.1f * happyHits).coerceAtMost(0.95f)
            else -> Mood.Neutral to 0.4f
        }
    }
}
