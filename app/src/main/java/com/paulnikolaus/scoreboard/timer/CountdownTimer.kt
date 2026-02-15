package com.paulnikolaus.scoreboard.timer

import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountdownTimer(
    private val scope: CoroutineScope,
    private val tickIntervalMs: Long = 50L
) {

    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunningFlow: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var totalDurationMs: Long = 0L
    private var endTimestamp: Long = 0L
    private var job: Job? = null

    fun setDuration(durationMs: Long) {
        totalDurationMs = durationMs
        _remainingMs.value = durationMs
    }

    fun start() {
        if (job?.isActive == true) return

        _isRunning.value = true

        endTimestamp = SystemClock.elapsedRealtime() + _remainingMs.value

        job = scope.launch {
            while (isActive) {

                val remaining =
                    endTimestamp - SystemClock.elapsedRealtime()

                if (remaining <= 0L) {
                    _remainingMs.value = 0L
                    stop()
                    break
                }

                _remainingMs.value = remaining
                delay(tickIntervalMs)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        _isRunning.value = false
    }

    fun reset() {
        stop()
        _remainingMs.value = totalDurationMs
    }

    fun isRunning(): Boolean = job?.isActive == true
}
