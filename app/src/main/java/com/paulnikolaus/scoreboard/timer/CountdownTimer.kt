package com.paulnikolaus.scoreboard.timer

import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A robust countdown timer that uses Coroutines and StateFlow.
 * It calculates time based on the system's monotonic clock to remain accurate
 * even if the thread is delayed or the device undergoes minor performance spikes.
 */
class CountdownTimer(
    private val scope: CoroutineScope,
    private val tickIntervalMs: Long = 50L // Default update interval (20 times per second)
) {

    // Current remaining time in milliseconds
    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    // Observes whether the timer is actively ticking
    private val _isRunning = MutableStateFlow(false)
    val isRunningFlow: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var totalDurationMs: Long = 0L
    private var endTimestamp: Long = 0L
    private var job: Job? = null

    /**
     * timeProvider uses elapsedRealtime() which is the time since the device booted.
     * Unlike Wall Clock time, this is not affected by time zone changes or manual clock updates.
     */
    private val timeProvider: () -> Long = { SystemClock.elapsedRealtime() }

    /**
     * Initializes or overrides the timer's duration.
     */
    fun setDuration(durationMs: Long) {
        totalDurationMs = durationMs
        _remainingMs.value = durationMs
    }

    /**
     * Starts the countdown from the current [_remainingMs].
     */
    fun start() {
        // Prevent multiple coroutines from running simultaneously
        if (job?.isActive == true) return

        _isRunning.value = true

        // Calculate the exact timestamp in the future when the timer should reach zero
        endTimestamp = timeProvider() + _remainingMs.value

        job = scope.launch {
            while (isActive) {
                // Determine how much time is left until the target timestamp
                val remaining = endTimestamp - timeProvider()

                if (remaining <= 0L) {
                    // Timer finished
                    _remainingMs.value = 0L
                    stop()
                    break
                }

                // Update the state flow for UI observers
                _remainingMs.value = remaining

                // Wait for the next tick
                delay(tickIntervalMs)
            }
        }
    }

    /**
     * Cancels the active coroutine and stops the timer.
     */
    fun stop() {
        job?.cancel()
        job = null
        _isRunning.value = false
    }

    /**
     * Checks if the timer is currently active.
     */
    fun isRunning(): Boolean = job?.isActive == true
}