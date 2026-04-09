package com.manish.mindora.presentation.insights

import com.manish.mindora.domain.model.JournalEntry
import java.util.Calendar
import java.util.Locale

object MoodHistoryAggregator {

    fun lastEntryPerDay(
        entries: List<JournalEntry>,
        dayCount: Int = 7,
        locale: Locale = Locale.getDefault(),
    ): List<DailyMoodSlot> {
        val cal = Calendar.getInstance(locale)
        val todayStart = startOfDayMillis(cal, System.currentTimeMillis())
        val slots = mutableListOf<DailyMoodSlot>()
        for (offset in dayCount - 1 downTo 0) {
            cal.timeInMillis = todayStart
            cal.add(Calendar.DAY_OF_MONTH, -offset)
            val dayStart = cal.timeInMillis
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val dayEndExclusive = cal.timeInMillis

            val dayEntries = entries.filter { it.createdAtEpochMillis in dayStart until dayEndExclusive }
            val latest = dayEntries.maxByOrNull { it.createdAtEpochMillis }
            slots += DailyMoodSlot(
                dayStartMillis = dayStart,
                mood = latest?.mood,
                confidence = latest?.confidence,
            )
        }
        return slots
    }

    private fun startOfDayMillis(cal: Calendar, millis: Long): Long {
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
