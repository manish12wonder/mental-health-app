package com.manish.mindora.data.sentiment

import com.manish.mindora.domain.model.Mood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Manual QA checklist (MVP):
 * - Fresh install: disclaimer blocks the rest of the app until acknowledged.
 * - After acknowledge: anonymous sign-in; saving a journal entry writes to Firestore under users/{uid}/entries.
 * - With SENTIMENT_API_KEY unset: keyword-based mood and string feedback still work.
 * - With a valid Hugging Face token in local.properties: online analysis is used when the network succeeds.
 * - Airplane mode or HF errors: entry still saves; mood falls back to keyword heuristic.
 * - Insights shows seven day slots and recent entries update after a save.
 */
class MoodMapperTest {

    private lateinit var mapper: MoodMapper

    @Before
    fun setup() {
        mapper = MoodMapper()
    }

    @Test
    fun huggingFace_joy_mapsToHappy() {
        val (mood, conf) = mapper.fromHuggingFaceLabel("joy", 0.9f)
        assertEquals(Mood.Happy, mood)
        assertTrue(conf >= 0.35f)
    }

    @Test
    fun huggingFace_lowScore_mapsToNeutral() {
        val (mood, _) = mapper.fromHuggingFaceLabel("joy", 0.1f)
        assertEquals(Mood.Neutral, mood)
    }

    @Test
    fun keyword_sadWords_mapsToSad() {
        val (mood, _) = mapper.fromKeywordHeuristic("I feel sad and lonely today")
        assertEquals(Mood.Sad, mood)
    }

    @Test
    fun keyword_angry_mapsToAngry() {
        val (mood, _) = mapper.fromKeywordHeuristic("I am furious and angry")
        assertEquals(Mood.Angry, mood)
    }
}
