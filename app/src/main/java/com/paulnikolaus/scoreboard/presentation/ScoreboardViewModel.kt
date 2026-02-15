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

class ScoreboardViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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

    private val gameClock =
        CountdownTimer(viewModelScope)

    private val shotClock =
        CountdownTimer(viewModelScope)

    val isShotClockRunning: StateFlow<Boolean>
        get() = shotClock.isRunningFlow

    val isGameClockRunning: StateFlow<Boolean>
        get() = gameClock.isRunningFlow

    // ----------------------------
    // Score State
    // ----------------------------

    private val _scoreState = MutableStateFlow(ScoreState())
    val scoreState: StateFlow<ScoreState> =
        _scoreState.asStateFlow()

    init {

        // ----------------------------
        // Restore Scores
        // ----------------------------

        val restoredHome = savedStateHandle.get<Int>(KEY_HOME) ?: 0
        val restoredAway = savedStateHandle.get<Int>(KEY_AWAY) ?: 0

        _scoreState.value = ScoreState(
            home = restoredHome,
            away = restoredAway
        )

        // ----------------------------
        // Restore Game Clock
        // ----------------------------

        val restoredGameTime =
            savedStateHandle.get<Long>(KEY_GAME_TIME)
                ?: (10 * 60_000L)

        val gameWasRunning =
            savedStateHandle.get<Boolean>(KEY_GAME_RUNNING)
                ?: false

        gameClock.setDuration(restoredGameTime)

        if (gameWasRunning) {
            gameClock.start()
        }

        // ----------------------------
        // Restore Shot Clock
        // ----------------------------

        val restoredShotTime =
            savedStateHandle.get<Long>(KEY_SHOT_TIME)
                ?: 24_000L

        val shotWasRunning =
            savedStateHandle.get<Boolean>(KEY_SHOT_RUNNING)
                ?: false

        shotClock.setDuration(restoredShotTime)

        if (shotWasRunning) {
            shotClock.start()
        }

        // ----------------------------
        // Persist Time Continuously
        // ----------------------------

        viewModelScope.launch {
            gameTime.collect { remaining ->
                savedStateHandle[KEY_GAME_TIME] = remaining
            }
        }

        viewModelScope.launch {
            shotTime.collect { remaining ->
                savedStateHandle[KEY_SHOT_TIME] = remaining
            }
        }
    }

    // ----------------------------
    // Exposed Timer Flows
    // ----------------------------

    val gameTime: StateFlow<Long>
        get() = gameClock.remainingMs

    val shotTime: StateFlow<Long>
        get() = shotClock.remainingMs

    // ----------------------------
    // Game Clock Controls
    // ----------------------------

    fun setGameDuration(minutes: Int, seconds: Int) {
        if (
            minutes in 0..60 &&
            seconds in 0..59
        ) {

            val totalMs = (minutes * 60 + seconds) * 1000L

            // Prevent values > 60:00
            if (totalMs <= 60 * 60_000L) {

                gameClock.stop()
                gameClock.setDuration(totalMs)

                savedStateHandle[KEY_GAME_TIME] = totalMs
                savedStateHandle[KEY_GAME_RUNNING] = false
            }
        }
    }



    fun toggleGameClock() {
        if (gameClock.isRunning()) {
            gameClock.stop()
        } else {
            gameClock.start()
        }

        savedStateHandle[KEY_GAME_RUNNING] = gameClock.isRunning()
    }

    fun resetGameClock() {
        gameClock.reset()

        savedStateHandle[KEY_GAME_RUNNING] = false

    }

    // ----------------------------
    // Shot Clock Controls
    // ----------------------------

    fun resetShotClock(seconds: Int) {
        shotClock.stop()                 // ensure it is stopped
        shotClock.setDuration(seconds * 1000L)

        savedStateHandle[KEY_SHOT_RUNNING] = false
    }


    fun toggleShotClock() {
        if (shotClock.isRunning()) {
            shotClock.stop()
        } else {
            shotClock.start()
        }

        savedStateHandle[KEY_SHOT_RUNNING] = shotClock.isRunning()

    }

    // ----------------------------
    // Score Controls
    // ----------------------------

    fun addScore(team: Team, points: Int) {

        _scoreState.value = when (team) {
            Team.HOME -> _scoreState.value.copy(
                home = _scoreState.value.home + points
            )

            Team.AWAY -> _scoreState.value.copy(
                away = _scoreState.value.away + points
            )
        }

        savedStateHandle[KEY_HOME] = _scoreState.value.home
        savedStateHandle[KEY_AWAY] = _scoreState.value.away
    }

    fun undoScore(team: Team) {

        _scoreState.value = when (team) {
            Team.HOME -> _scoreState.value.copy(
                home = (_scoreState.value.home - 1).coerceAtLeast(0)
            )

            Team.AWAY -> _scoreState.value.copy(
                away = (_scoreState.value.away - 1).coerceAtLeast(0)
            )
        }

        savedStateHandle[KEY_HOME] = _scoreState.value.home
        savedStateHandle[KEY_AWAY] = _scoreState.value.away
    }

    fun resetScores() {
        _scoreState.value = ScoreState()

        savedStateHandle[KEY_HOME] = _scoreState.value.home
        savedStateHandle[KEY_AWAY] = _scoreState.value.away
    }
}
