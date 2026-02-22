package com.paulnikolaus.scoreboard

import androidx.lifecycle.SavedStateHandle
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [ScoreboardViewModel].
 * These tests verify the logic for scoring, undoing points, resetting
 * the game state, and the synchronization between the Game and Shot clocks.
 */
class ScoreboardViewModelTest {

    private lateinit var viewModel: ScoreboardViewModel

    /**
     * Initializes a fresh ViewModel before each test.
     * We provide a real [SavedStateHandle] to simulate state persistence.
     */
    @Before
    fun setup() {
        val savedStateHandle = SavedStateHandle()

        // Provide a "fake" time provider that returns 0.
        // This stops the "SystemClock not mocked" error.
        viewModel = ScoreboardViewModel(
            savedStateHandle = savedStateHandle,
            gameTimeProvider = { 0L },
            shotTimeProvider = { 0L }
        )
    }

    @Test
    fun addScore_home_increasesScoreCorrectly() {
        // Increment home score by 2 points
        viewModel.addScore(Team.HOME, 2)
        val score = viewModel.scoreState.value

        assertEquals(2, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun undoScore_doesNotGoBelowZero() {
        // Attempt to decrement score when it is already at 0
        viewModel.undoScore(Team.HOME)
        val score = viewModel.scoreState.value

        assertEquals(0, score.home)
    }

    @Test
    fun resetScores_setsBothToZero() {
        viewModel.addScore(Team.HOME, 5)
        viewModel.addScore(Team.AWAY, 3)
        viewModel.resetScores()

        val score = viewModel.scoreState.value
        assertEquals(0, score.home)
        assertEquals(0, score.away)
    }

    /**
     * Verifies the "Linked Clock" constraint.
     * Rule: When the Game Clock stops, the Shot Clock must also stop.
     * Rule: When the Shot Clock stops, the Game Clock should NOT be affected.
     */
    @Test
    fun gameClockStop_stopsShotClock_butNotViceVersa() {
        // 1. Start both clocks
        viewModel.toggleGameClock()
        viewModel.toggleShotClock()

        assertTrue("Game clock should be running", viewModel.isGameClockRunning.value)
        assertTrue("Shot clock should be running", viewModel.isShotClockRunning.value)

        // 2. Stop the Game Clock
        viewModel.toggleGameClock()

        assertFalse("Game clock should be stopped", viewModel.isGameClockRunning.value)
        assertFalse("Shot clock should be stopped automatically", viewModel.isShotClockRunning.value)

        // 3. Start both again
        viewModel.toggleGameClock()
        viewModel.toggleShotClock()

        // 4. Stop ONLY the Shot Clock
        viewModel.toggleShotClock()

        assertTrue("Game clock should still be running", viewModel.isGameClockRunning.value)
        assertFalse("Shot clock should be stopped", viewModel.isShotClockRunning.value)
    }

    @Test
    fun multipleAddScore_accumulatesCorrectly() {
        viewModel.addScore(Team.HOME, 2)
        viewModel.addScore(Team.HOME, 3)
        assertEquals(5, viewModel.scoreState.value.home)
    }
}