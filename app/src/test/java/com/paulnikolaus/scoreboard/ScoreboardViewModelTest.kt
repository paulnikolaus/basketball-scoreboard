package com.paulnikolaus.scoreboard

import androidx.lifecycle.SavedStateHandle
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Unit tests for the [ScoreboardViewModel].
 * These tests verify the logic for scoring, undoing points, resetting
 * the game state, and the synchronization between the Game and Shot clocks.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScoreboardViewModelTest {

    private lateinit var viewModel: ScoreboardViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    // Runs viewModelScope coroutines under our control instead of on a background thread
    private val testDispatcher = StandardTestDispatcher()

    // Fake clock in milliseconds. Tests move time forward by assigning to it.
    private var now = 0L

    /**
     * Initializes a fresh ViewModel before each test.
     * We provide a real [SavedStateHandle] to simulate state persistence.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()

        // Provide a fake time provider instead of SystemClock.
        // This stops the "SystemClock not mocked" error.
        viewModel = ScoreboardViewModel(
            savedStateHandle = savedStateHandle,
            gameTimeProvider = { now },
            shotTimeProvider = { now }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

    /**
     * Verifies that the shot clock stops when the game clock runs out on its own
     * (not only when the user presses Stop), and that no stale "running" flags are saved.
     */
    @Test
    fun gameClockExpiry_stopsShotClock() = runTest(testDispatcher) {
        // 1. Set a 1-second game and start both clocks
        viewModel.setGameTimeIfValid(0, 1)
        viewModel.toggleGameClock()
        viewModel.toggleShotClock()
        runCurrent() // Let the timers and observers start

        // 2. Move the fake clock past the end of the game and let the timers tick
        now = 1_000L
        advanceTimeBy(100.milliseconds)
        runCurrent()

        // 3. The game is over: both clocks stopped, buzzer fired, nothing restarts after process death
        assertEquals(0L, viewModel.gameTime.value)
        assertTrue("Game buzzer should fire", viewModel.gameBuzzerEvent.value)
        assertFalse("Game clock should be stopped", viewModel.isGameClockRunning.value)
        assertFalse("Shot clock should stop with the game clock", viewModel.isShotClockRunning.value)
        assertEquals(false, savedStateHandle.get<Boolean>("game_running"))
        assertEquals(false, savedStateHandle.get<Boolean>("shot_running"))
    }

    @Test
    fun multipleAddScore_accumulatesCorrectly() {
        viewModel.addScore(Team.HOME, 2)
        viewModel.addScore(Team.HOME, 3)
        assertEquals(5, viewModel.scoreState.value.home)
    }
}