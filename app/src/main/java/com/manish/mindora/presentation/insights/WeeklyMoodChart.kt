package com.manish.mindora.presentation.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.ui.theme.MindoraMoodChart

private fun moodBarHeight(mood: Mood?): Float = when (mood) {
    null -> 0.08f
    Mood.Happy -> 1f
    Mood.Neutral -> 0.65f
    Mood.Sad -> 0.4f
    Mood.Angry -> 0.25f
}

private fun moodBarColor(mood: Mood?, isDark: Boolean): Color {
    val base = when (mood) {
        null -> MindoraMoodChart.Empty
        Mood.Happy -> MindoraMoodChart.Happy
        Mood.Neutral -> MindoraMoodChart.Neutral
        Mood.Sad -> MindoraMoodChart.Sad
        Mood.Angry -> MindoraMoodChart.Angry
    }
    return if (isDark) base.copy(alpha = 0.92f) else base
}

@Composable
fun WeeklyMoodChart(
    slots: List<DailyMoodSlot>,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.4f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
    ) {
        if (slots.isEmpty()) return@Canvas
        val barCount = slots.size
        val spacing = size.width * 0.02f
        val totalSpacing = spacing * (barCount + 1)
        val barWidth = (size.width - totalSpacing) / barCount
        val maxBarHeight = size.height * 0.62f
        val baselineY = size.height * 0.78f

        slots.forEachIndexed { index, slot ->
            val x = spacing + index * (barWidth + spacing)
            val h = maxBarHeight * moodBarHeight(slot.mood)
            val top = baselineY - h
            drawRoundRect(
                color = moodBarColor(slot.mood, isDark),
                topLeft = Offset(x, top),
                size = Size(barWidth, h.coerceAtLeast(size.height * 0.02f)),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
    }
}

private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}
