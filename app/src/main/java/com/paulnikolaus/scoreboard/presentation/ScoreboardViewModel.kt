package com.paulnikolaus.scoreboard.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulnikolaus.scoreboard.data.ScoreState
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.timer.CountdownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.paulnikolaus.scoreboard.domain.GameTimeValidator

/**
 * ViewModel responsible for managing the scoreboard state, including scores,
 * game timers, and shot clocks. It uses [SavedStateHandle] to ensure data
 * survives process death or configuration changes.
 */
class ScoreboardViewModel(
    private val savedStateHandle: SavedStateHandle,
    // Add these optional providers for testing
    gameTimeProvider: () -> Long = { android.os.SystemClock.elapsedRealtime() },
    shotTimeProvider: () -> Long = { android.os.SystemClock.elapsedRealtime() }
) : ViewModel() {

    // Keys used for saving and restoring state from SavedStateHandle
    companion object {
        private const val KEY_HOME = "home_score"
        private const val KEY_AWAY = "away_score"
        private const val KEY_GAME_TIME = "game_time"
        private const val KEY_SHOT_TIME = "shot_time"
        private const val KEY_GAME_RUNNING = "game_running"
        private const val KEY_SHOT_RUNNING = "shot_running"
    }

    // ----------------------------
    // Timers
    // ----------------------------

    // The main game clock (e.g., 10:00)
    private val gameClock = CountdownTimer(viewModelScope, timeProvider = gameTimeProvider)

    // The shot clock (e.g., 24s or 14s)
    private val shotClock = CountdownTimer(viewModelScope, timeProvider = shotTimeProvider)

    // Exposed flows to observe if timers are currently ticking
    val isShotClockRunning: StateFlow<Boolean> = shotClock.isRunningFlow
    val isGameClockRunning: StateFlow<Boolean> = gameClock.isRunningFlow

    // ----------------------------
    // Buzzer Timer Events
    // ----------------------------

    // These events trigger the buzzer sound in the UI layer
    private val _gameBuzzerEvent = MutableStateFlow(false)
    val gameBuzzerEvent: StateFlow<Boolean> = _gameBuzzerEvent

    private val _shotBuzzerEvent = MutableStateFlow(false)
    val shotBuzzerEvent: StateFlow<Boolean> = _shotBuzzerEvent

    // ----------------------------
    // Score State
    // ----------------------------

    private val _scoreState = MutableStateFlow(ScoreState())
    val scoreState: StateFlow<ScoreState> = _scoreState.asStateFlow()

    // ----------------------------
    // Dialog State
    // ----------------------------

    private val _showGameDialog = MutableStateFlow(false)
    val showGameDialog: StateFlow<Boolean> = _showGameDialog.asStateFlow()

    fun openGameDialog() { _showGameDialog.value = true }
    fun closeGameDialog() { _showGameDialog.value = false }

    init {
        // --- Restore Scores from bundle ---
        val restoredHome = savedStateHandle.get<Int>(KEY_HOME) ?: 0
        val restoredAway = savedStateHandle.get<Int>(KEY_AWAY) ?: 0
        _scoreState.value = ScoreState(home = restoredHome, away = restoredAway)

        // --- Restore Game Clock ---
        val restoredGameTime = savedStateHandle.get<Long>(KEY_GAME_TIME) ?: (10 * 60_000L)
        val gameWasRunning = savedStateHandle.get<Boolean>(KEY_GAME_RUNNING) ?: false
        gameClock.setDuration(restoredGameTime)
        if (gameWasRunning) gameClock.start()

        // --- Restore Shot Clock ---
        val restoredShotTime = savedStateHandle.get<Long>(KEY_SHOT_TIME) ?: 24_000L
        val shotWasRunning = savedStateHandle.get<Boolean>(KEY_SHOT_RUNNING) ?: false
        shotClock.setDuration(restoredShotTime)
        if (shotWasRunning) shotClock.start()

        // --- Persistence Observers ---
        // Automatically save timer progress to SavedStateHandle as it ticks
        viewModelScope.launch {
            gameTime.collect { remaining -> savedStateHandle[KEY_GAME_TIME] = remaining }
        }
        viewModelScope.launch {
            shotTime.collect { remaining -> savedStateHandle[KEY_SHOT_TIME] = remaining }
        }

        // --- Game Buzzer Detection ---
        // Triggers buzzer when timer transitions from positive to zero
        viewModelScope.launch {
            var previous = gameClock.remainingMs.value
            gameTime.collect { current ->
                if (previous > 0 && current == 0L) {
                    _gameBuzzerEvent.value = true
                }
                previous = current
            }
        }

        // --- Shot Buzzer Detection ---
        viewModelScope.launch {
            var previous = shotClock.remainingMs.value
            shotTime.collect { current ->
                if (previous > 0 && current == 0L) {
                    _shotBuzzerEvent.value = true
                }
                previous = current
            }
        }
    }

    // ----------------------------
    // Exposed Timer Flows
    // ----------------------------

    val gameTime: StateFlow<Long> get() = gameClock.remainingMs
    val shotTime: StateFlow<Long> get() = shotClock.remainingMs

    // ----------------------------
    // Game Clock Controls
    // ----------------------------

    /**
     * Updates the game clock to a specific time and stops it.
     */
    fun setGameDuration(minutes: Int, seconds: Int) {
        val totalMs = (minutes * 60 + seconds) * 1000L
        gameClock.stop()
        gameClock.setDuration(totalMs)

        savedStateHandle[KEY_GAME_TIME] = totalMs
        savedStateHandle[KEY_GAME_RUNNING] = false
    }

    /**
     * Toggles the main game clock on or off.
     * If the game clock is stopped, the shot clock is also stopped automatically.
     */
    fun toggleGameClock() {
        if (gameClock.isRunning()) {
            // Stop the game clock
            gameClock.stop()

            // NEW CONSTRAINT: If game stops, shot clock MUST stop
            if (shotClock.isRunning()) {
                shotClock.stop()
                savedStateHandle[KEY_SHOT_RUNNING] = false
            }
        } else {
            // Start the game clock
            gameClock.start()
        }

        // Sync the state to handle process death
        savedStateHandle[KEY_GAME_RUNNING] = gameClock.isRunning()
    }

    // ----------------------------
    // Shot Clock Controls
    // ----------------------------

    /**
     * Resets the shot clock to a specific value (usually 24 or 14).
     */
    fun resetShotClock(seconds: Int) {
        shotClock.stop()
        shotClock.setDuration(seconds * 1000L)
        savedStateHandle[KEY_SHOT_RUNNING] = false
    }

    fun toggleShotClock() {
        if (shotClock.isRunning()) shotClock.stop() else shotClock.start()
        savedStateHandle[KEY_SHOT_RUNNING] = shotClock.isRunning()
    }

    // ----------------------------
    // Score Controls
    // ----------------------------

    /**
     * Adds specific points to either the Home or Away team.
     */
    fun addScore(team: Team, points: Int) {
        _scoreState.value = when (team) {
            Team.HOME -> _scoreState.value.copy(home = _scoreState.value.home + points)
            Team.AWAY -> _scoreState.value.copy(away = _scoreState.value.away + points)
        }
        syncScoresToSavedState()
    }

    /**
     * Decrements the score for a team by 1 (minimum 0).
     */
    fun undoScore(team: Team) {
        _scoreState.value = when (team) {
            Team.HOME -> _scoreState.value.copy(home = (_scoreState.value.home - 1).coerceAtLeast(0))
            Team.AWAY -> _scoreState.value.copy(away = (_scoreState.value.away - 1).coerceAtLeast(0))
        }
        syncScoresToSavedState()
    }

    /**
     * Resets both scores to zero.
     */
    fun resetScores() {
        _scoreState.value = ScoreState()
        syncScoresToSavedState()
    }

    private fun syncScoresToSavedState() {
        savedStateHandle[KEY_HOME] = _scoreState.value.home
        savedStateHandle[KEY_AWAY] = _scoreState.value.away
    }

    // ----------------------------
    // Buzzer Consumption
    // ----------------------------

    // Resets buzzer flags once the UI has handled the sound/animation
    fun consumeGameBuzzer() { _gameBuzzerEvent.value = false }
    fun consumeShotBuzzer() { _shotBuzzerEvent.value = false }

    /**
     * Validates input before updating the game clock duration.
     * @return True if successful, False if validation failed.
     */
    fun setGameTimeIfValid(minutes: Int, seconds: Int): Boolean {
        return if (GameTimeValidator.isValid(minutes, seconds)) {
            setGameDuration(minutes, seconds)
            true
        } else {
            false
        }
    }
}