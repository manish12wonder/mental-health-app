package com.manish.mindora.presentation.home

import com.manish.mindora.MainDispatcherRule
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.fakes.FakeJournalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun todayEntry_surfacesLatestForLocalDay() = runTest(mainDispatcherRule.testDispatcher) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 14)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val noonToday = cal.timeInMillis

        val entry = JournalEntry(
            id = "1",
            userId = "u",
            text = "Hello",
            createdAtEpochMillis = noonToday,
            mood = Mood.Happy,
            confidence = 0.8f,
            feedback = "Nice",
        )
        val fake = FakeJournalRepository(listOf(entry))
        val vm = HomeViewModel(fake)
        advanceUntilIdle()
        val state = vm.uiState.value
        assertNotNull(state.todayEntry)
        assertEquals(Mood.Happy, state.todayEntry?.mood)
    }
}
