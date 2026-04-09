package com.manish.mindora.data.feedback

import android.content.Context
import com.manish.mindora.R
import com.manish.mindora.domain.feedback.FeedbackMessageProvider
import com.manish.mindora.domain.model.Mood
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AndroidFeedbackMessageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : FeedbackMessageProvider {

    private val random = Random.Default

    override fun messageFor(mood: Mood): String {
        val res = context.resources
        val arr = when (mood) {
            Mood.Happy -> res.getStringArray(R.array.feedback_happy)
            Mood.Sad -> res.getStringArray(R.array.feedback_sad)
            Mood.Angry -> res.getStringArray(R.array.feedback_angry)
            Mood.Neutral -> res.getStringArray(R.array.feedback_neutral)
        }
        return arr[random.nextInt(arr.size)]
    }
}
