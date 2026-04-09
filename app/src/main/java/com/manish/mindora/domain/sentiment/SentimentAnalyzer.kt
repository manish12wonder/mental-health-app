package com.manish.mindora.domain.sentiment

import com.manish.mindora.domain.model.SentimentAnalysis

interface SentimentAnalyzer {
    suspend fun analyze(text: String): SentimentAnalysis
}
