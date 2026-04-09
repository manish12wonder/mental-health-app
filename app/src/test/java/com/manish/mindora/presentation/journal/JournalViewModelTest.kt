package com.manish.mindora.presentation.journal

import com.manish.mindora.MainDispatcherRule
import com.manish.mindora.fakes.FakeJournalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun saveEntry_emitsSuccess() = runTest(mainDispatcherRule.testDispatcher) {
        val fake = FakeJournalRepository()
        val vm = JournalViewModel(fake)
        vm.saveEntry("Grateful for small wins today", emptySet())
        advanceUntilIdle()
        val state = vm.saveUiState.value
        assertTrue(state is JournalSaveUiState.Success)
    }
}
