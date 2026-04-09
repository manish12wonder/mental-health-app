package com.manish.mindora

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.manish.mindora.testutil.clearMindoraLocalStorage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests: full onboarding, every tab, journal input + save, settings, and Espresso sync.
 *
 * Uses [createAndroidComposeRule] (single-argument overload) + [Espresso.onIdle] for main-thread sync.
 */
@RunWith(AndroidJUnit4::class)
class MindoraEndToEndInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val targetContext get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun str(resId: Int): String = targetContext.getString(resId)

    private fun str(resId: Int, vararg formatArgs: Any): String =
        targetContext.getString(resId, *formatArgs)

    /** Sync Compose + main thread without relying on optional APIs. */
    private fun idle() {
        Espresso.onIdle()
    }

    @Before
    fun resetLocalDataAndRecreateActivity() {
        clearMindoraLocalStorage(targetContext)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            composeRule.activity.recreate()
        }
        idle()
    }

    private fun dismissDisclaimerIfPresent() {
        val acknowledge = str(R.string.disclaimer_acknowledge)
        idle()
        runCatching {
            val nodes = composeRule.onAllNodesWithText(acknowledge, useUnmergedTree = true)
                .fetchSemanticsNodes()
            if (nodes.isNotEmpty()) {
                composeRule.onNodeWithText(acknowledge, useUnmergedTree = true).performClick()
                idle()
            }
        }
    }

    private fun completeNameSetupIfPresent() {
        val title = str(R.string.name_setup_title)
        runCatching {
            if (composeRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()) {
                composeRule.onNodeWithTag("onboarding_name_input").performClick()
                composeRule.onNodeWithTag("onboarding_name_input").performTextInput("UiTestUser")
                composeRule.onNodeWithText(str(R.string.name_setup_continue)).performClick()
                idle()
            }
        }
    }

    private fun skipOnboarding() {
        dismissDisclaimerIfPresent()
        completeNameSetupIfPresent()
        idle()
    }

    @Test
    fun onboardingThenHome_showsBottomNav() {
        skipOnboarding()
        composeRule.onNodeWithText(str(R.string.nav_home), useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun navigateToJournal_showsComposer() {
        skipOnboarding()
        composeRule.onNodeWithText(str(R.string.nav_journal)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.journal_header_primary)).assertIsDisplayed()
    }

    @Test
    fun espressoOnIdle_activityContentDisplayed() {
        skipOnboarding()
        Espresso.onIdle()
        Espresso.onView(withId(android.R.id.content)).check(matches(isDisplayed()))
    }

    /** Step-by-step: disclaimer title → acknowledge → name title → type → Continue → Home greeting. */
    @Test
    fun fullOnboarding_disclaimerAndNameEntry_showsPersonalizedHome() {
        composeRule.waitUntil(12_000) {
            composeRule.onAllNodesWithText(str(R.string.disclaimer_title), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(str(R.string.disclaimer_title), useUnmergedTree = true)
            .assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.disclaimer_acknowledge), useUnmergedTree = true)
            .performClick()
        idle()

        composeRule.waitUntil(12_000) {
            composeRule.onAllNodesWithText(str(R.string.name_setup_title)).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(str(R.string.name_setup_subtitle)).assertIsDisplayed()

        composeRule.onNodeWithTag("onboarding_name_input").performClick()
        composeRule.onNodeWithTag("onboarding_name_input").performTextInput("Alex")

        composeRule.onNodeWithText(str(R.string.name_setup_continue)).assertIsEnabled()
        composeRule.onNodeWithText(str(R.string.name_setup_continue)).performClick()
        idle()

        composeRule.waitUntil(15_000) {
            composeRule.onAllNodesWithText("Alex", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(str(R.string.home_hey_user, "Alex"), substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.home_today_mood_heading)).assertIsDisplayed()
    }

    /** Visits Journal, Insights, AI, Home and opens Settings (theme) and returns. */
    @Test
    fun navigateAllMainTabs_andSettingsRoundTrip() {
        skipOnboarding()

        composeRule.onNodeWithText(str(R.string.nav_journal)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.journal_header_primary)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.nav_insights)).performClick()
        idle()
        composeRule.onAllNodesWithText(str(R.string.insights_title))[0].assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.insights_weekly_trend)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.nav_ai)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.ai_feedback_title)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.nav_home)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.home_today_mood_heading)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(str(R.string.settings_open_content_description))
            .performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.settings_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_theme_section)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.theme_dark)).performClick()
        idle()

        composeRule.onNodeWithContentDescription(str(R.string.settings_back)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.home_today_mood_heading)).assertIsDisplayed()
    }

    /** Journal: chip tap, body text, Save & Analyze, wait until editor clears (save finished). */
    @LargeTest
    @Test
    fun journal_selectTag_typeEntry_clickSave_clearsComposer() {
        skipOnboarding()
        composeRule.onNodeWithText(str(R.string.nav_journal)).performClick()
        idle()

        composeRule.onNodeWithText(str(R.string.journal_tag_reflective)).performClick()
        idle()

        composeRule.onNodeWithTag("journal_body_input").performClick()
        composeRule.onNodeWithTag("journal_body_input").performTextInput(
            "Grateful for small wins today. Testing the full journal flow.",
        )
        idle()

        composeRule.onNodeWithTag("journal_save_button").performScrollTo()
        composeRule.onNodeWithTag("journal_save_button").assertIsEnabled()
        composeRule.onNodeWithTag("journal_save_button").performClick()

        val zeroWordsLine = str(R.string.journal_word_count_format, 0, 500)
        composeRule.waitUntil(45_000) {
            composeRule.onAllNodesWithText(zeroWordsLine).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(zeroWordsLine).assertIsDisplayed()
    }

    /**
     * Single long scenario: cold start → disclaimer → name → home → all tabs → journal save →
     * insights → AI → settings → back home.
     */
    @LargeTest
    @Test
    fun fullAppFlow_disclaimerThroughJournalSaveInsightsAiAndSettings() {
        composeRule.waitUntil(12_000) {
            composeRule.onAllNodesWithText(str(R.string.disclaimer_title), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(str(R.string.disclaimer_acknowledge), useUnmergedTree = true)
            .performClick()
        idle()

        composeRule.waitUntil(12_000) {
            composeRule.onAllNodesWithText(str(R.string.name_setup_title)).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("onboarding_name_input").performClick()
        composeRule.onNodeWithTag("onboarding_name_input").performTextInput("Journey")
        composeRule.onNodeWithText(str(R.string.name_setup_continue)).performClick()
        idle()

        composeRule.waitUntil(15_000) {
            composeRule.onAllNodesWithText("Journey", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText(str(R.string.nav_journal)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.journal_tag_grateful)).performClick()
        composeRule.onNodeWithTag("journal_body_input").performClick()
        composeRule.onNodeWithTag("journal_body_input").performTextInput("End to end UI journey complete.")
        composeRule.onNodeWithTag("journal_save_button").performScrollTo()
        composeRule.onNodeWithTag("journal_save_button").performClick()

        val zeroWordsLine = str(R.string.journal_word_count_format, 0, 500)
        composeRule.waitUntil(45_000) {
            composeRule.onAllNodesWithText(zeroWordsLine).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText(str(R.string.nav_insights)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.insights_recent_title)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.nav_ai)).performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.ai_feedback_title)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.nav_home)).performClick()
        idle()
        composeRule.onNodeWithContentDescription(str(R.string.settings_open_content_description))
            .performClick()
        idle()
        composeRule.onNodeWithText(str(R.string.theme_light)).performClick()
        composeRule.onNodeWithContentDescription(str(R.string.settings_back)).performClick()
        idle()

        Espresso.onIdle()
        composeRule.onNodeWithText(str(R.string.home_today_mood_heading)).assertIsDisplayed()
    }
}
