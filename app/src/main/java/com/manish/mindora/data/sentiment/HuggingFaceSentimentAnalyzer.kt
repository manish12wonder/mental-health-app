package com.manish.mindora.data.sentiment

import com.manish.mindora.domain.model.SentimentAnalysis
import com.manish.mindora.domain.sentiment.SentimentAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

/**
 * Uses Hugging Face Inference API (cardiffnlp/twitter-roberta-base-emotion).
 * Set [SENTIMENT_API_KEY] in local.properties for remote analysis; otherwise use [CompositeSentimentAnalyzer] fallback.
 */
@Singleton
class HuggingFaceSentimentAnalyzer @Inject constructor(
    private val client: OkHttpClient,
    private val moodMapper: MoodMapper,
    @Named("sentiment_api_key") private val apiKey: String,
) : SentimentAnalyzer {

    override suspend fun analyze(text: String): SentimentAnalysis = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext SentimentAnalysis(
                mood = com.manish.mindora.domain.model.Mood.Neutral,
                confidence = 0f,
                rawLabel = "empty",
            )
        }
        if (apiKey.isBlank()) {
            throw IOException("Missing SENTIMENT_API_KEY")
        }

        val payload = buildJsonObject {
            put("inputs", trimmed)
        }
        val bodyString = Json.encodeToString(payload)
        val request = Request.Builder()
            .url(HF_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(bodyString.toRequestBody(JSON_MEDIA))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("HF HTTP ${response.code}")
        }
        val responseBody = response.body?.string().orEmpty()
        val root = Json.parseToJsonElement(responseBody)
        val labelScores = parseLabelScores(root)
        if (labelScores.isEmpty()) {
            throw IOException("No labels in HF response")
        }
        val (bestLabel, bestScore) = labelScores.maxBy { it.second }
        val (mood, confidence) = moodMapper.fromHuggingFaceLabel(bestLabel, bestScore.toFloat())
        SentimentAnalysis(mood, confidence, bestLabel)
    }

    private fun parseLabelScores(root: JsonElement): List<Pair<String, Double>> {
        val outer = root.jsonArray
        if (outer.isEmpty()) return emptyList()
        val items: JsonArray = when (val first = outer[0]) {
            is JsonObject -> outer
            is JsonArray -> first.jsonArray
            else -> return emptyList()
        }
        return items.mapNotNull { el ->
            val obj = el.jsonObject
            val label = obj["label"]?.jsonPrimitive?.content ?: return@mapNotNull null
            val score = obj["score"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
            label to score
        }
    }

    companion object {
        private const val HF_URL =
            "https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-emotion"
    }
}
