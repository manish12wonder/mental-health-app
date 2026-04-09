package com.manish.mindora.data.sentiment

import com.manish.mindora.domain.model.SentimentAnalysis
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Tries remote Hugging Face when API key is set; on failure or blank key uses local keyword heuristic.
 */
@Singleton
class CompositeSentimentAnalyzer @Inject constructor(
    private val remote: HuggingFaceSentimentAnalyzer,
    private val local: LocalKeywordSentimentAnalyzer,
    @Named("sentiment_api_key") private val apiKey: String,
) : SentimentAnalyzer {

    override suspend fun analyze(text: String): SentimentAnalysis {
        if (apiKey.isBlank()) {
            return local.analyze(text)
        }
        return try {
            remote.analyze(text)
        } catch (_: Exception) {
            local.analyze(text)
        }
    }
}
