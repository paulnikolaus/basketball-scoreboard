package com.paulnikolaus.scoreboard

import androidx.lifecycle.SavedStateHandle
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [ScoreboardViewModel].
 * These tests verify the logic for scoring, undoing points, and resetting
 * the game state while ensuring the ViewModel correctly interacts with [SavedStateHandle].
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
        viewModel = ScoreboardViewModel(savedStateHandle)
    }

    @Test
    fun addScore_home_increasesScoreCorrectly() {
        // Increment home score by 2 points (e.g., a standard basket)
        viewModel.addScore(Team.HOME, 2)

        val score = viewModel.scoreState.value

        // Verify Home is 2 and Away remains untouched
        assertEquals(2, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun undoScore_doesNotGoBelowZero() {
        // Attempt to decrement score when it is already at 0
        viewModel.undoScore(Team.HOME)

        val score = viewModel.scoreState.value

        // Verify the score is clamped at 0 and doesn't become -1
        assertEquals(0, score.home)
    }

    @Test
    fun resetScores_setsBothToZero() {
        // Set some initial points for both teams
        viewModel.addScore(Team.HOME, 5)
        viewModel.addScore(Team.AWAY, 3)

        // Reset the entire scoreboard
        viewModel.resetScores()

        val score = viewModel.scoreState.value

        // Verify both scores are back to initial state
        assertEquals(0, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun multipleAddScore_accumulatesCorrectly() {
        // Simulate a sequence of scoring events (2-pointer then a 3-pointer)
        viewModel.addScore(Team.HOME, 2)
        viewModel.addScore(Team.HOME, 3)

        // Verify the sum is correct
        assertEquals(5, viewModel.scoreState.value.home)
    }
}