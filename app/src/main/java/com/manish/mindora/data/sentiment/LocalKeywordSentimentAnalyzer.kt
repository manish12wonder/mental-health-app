package com.manish.mindora.data.sentiment

import com.manish.mindora.domain.model.SentimentAnalysis
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalKeywordSentimentAnalyzer @Inject constructor(
    private val moodMapper: MoodMapper,
) : SentimentAnalyzer {

    override suspend fun analyze(text: String): SentimentAnalysis {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return SentimentAnalysis(
                mood = com.manish.mindora.domain.model.Mood.Neutral,
                confidence = 0f,
                rawLabel = "empty",
            )
        }
        val (mood, confidence) = moodMapper.fromKeywordHeuristic(trimmed)
        return SentimentAnalysis(
            mood = mood,
            confidence = confidence,
            rawLabel = "keyword",
        )
    }
}
