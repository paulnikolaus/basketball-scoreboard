package com.paulnikolaus.scoreboard

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.presentation.ScoreboardScreen
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for [ScoreboardScreen], run on a device or emulator.
 * They check the Reset confirmation dialog, the Reset button in landscape,
 * and that open dialogs survive a rotation (saved-state restore).
 */
@RunWith(AndroidJUnit4::class)
class ScoreboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ScoreboardViewModel

    @Before
    fun setup() {
        // Created outside the composition so it survives a simulated rotation, like a real ViewModel
        composeTestRule.runOnUiThread {
            viewModel = ScoreboardViewModel(SavedStateHandle())
        }
    }

    @Composable
    private fun Screen() {
        ScoreboardScreen(viewModel = viewModel, isDarkMode = false, onToggleDarkMode = {})
    }

    @Test
    fun resetButton_asksForConfirmationBeforeResetting() {
        composeTestRule.setContent { Screen() }
        composeTestRule.runOnUiThread { viewModel.addScore(Team.HOME, 2) }

        // 1. Tapping Reset only opens the dialog; Cancel keeps the score
        composeTestRule.onNodeWithText("RESET SCORE").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Reset Score?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithText("Reset Score?").assertDoesNotExist()
        assertEquals(2, viewModel.scoreState.value.home)

        // 2. Confirming actually resets the score
        composeTestRule.onNodeWithText("RESET SCORE").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Reset").performClick()
        composeTestRule.onNodeWithText("Reset Score?").assertDoesNotExist()
        assertEquals(0, viewModel.scoreState.value.home)
    }

    @Test
    fun resetButton_isShownInLandscape() {
        composeTestRule.setContent {
            // Pretend the phone is sideways without actually rotating the emulator
            val landscape = Configuration(LocalConfiguration.current).apply {
                orientation = Configuration.ORIENTATION_LANDSCAPE
            }
            CompositionLocalProvider(LocalConfiguration provides landscape) { Screen() }
        }

        composeTestRule.onNodeWithText("RESET SCORE").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun resetDialog_staysOpenAfterRotation() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent { Screen() }

        composeTestRule.onNodeWithText("RESET SCORE").performScrollTo().performClick()
        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText("Reset Score?").assertIsDisplayed()
    }

    @Test
    fun settingsDialog_staysOpenAfterRotation() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent { Screen() }

        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }

    @Test
    fun setTimeDialog_keepsTypedValuesAfterRotation() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent { Screen() }

        // Open the dialog and type 12 into the minutes field
        composeTestRule.onNodeWithText("Set Time").performScrollTo().performClick()
        val minutesField = composeTestRule.onAllNodes(hasSetTextAction())[0]
        minutesField.performTextReplacement("12")

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText("Set Game Time (MM : SS)").assertIsDisplayed()
        composeTestRule.onAllNodes(hasSetTextAction())[0].assertTextContains("12")
    }
}
