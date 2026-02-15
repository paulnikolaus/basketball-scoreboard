package com.paulnikolaus.scoreboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulnikolaus.scoreboard.data.ScoreState
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.timer.CountdownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScoreboardViewModel : ViewModel() {

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



    init {
        // Default game clock = 10 minutes
        gameClock.setDuration(10 * 60_000L)

        // Optional: initialize shot clock to 24 seconds
        shotClock.setDuration(24_000L)
    }


    // ----------------------------
    // Score State
    // ----------------------------

    private val _scoreState = MutableStateFlow(ScoreState())
    val scoreState: StateFlow<ScoreState> =
        _scoreState.asStateFlow()

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

    fun setGameDuration(minutes: Int) {
        gameClock.setDuration(minutes * 60_000L)
    }

    fun toggleGameClock() {
        if (gameClock.isRunning()) {
            gameClock.stop()
        } else {
            gameClock.start()
        }
    }

    fun resetGameClock() {
        gameClock.reset()
    }

    // ----------------------------
    // Shot Clock Controls
    // ----------------------------

    fun resetShotClock(seconds: Int) {
        shotClock.stop()                 // ensure it is stopped
        shotClock.setDuration(seconds * 1000L)
    }


    fun toggleShotClock() {
        if (shotClock.isRunning()) {
            shotClock.stop()
        } else {
            shotClock.start()
        }
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
    }

    fun resetScores() {
        _scoreState.value = ScoreState()
    }
}
