package com.paulnikolaus.scoreboard.domain

/**
 * Validator object responsible for ensuring the game duration entered by the user
 * falls within acceptable limits for the scoreboard timer.
 */
object GameTimeValidator {

    /**
     * Checks if the provided minutes and seconds form a valid game duration.
     *
     * @param minutes The minute component of the timer (0-60).
     * @param seconds The second component of the timer (0-59).
     * @return True if the time is valid and greater than zero, false otherwise.
     */
    fun isValid(minutes: Int, seconds: Int): Boolean {
        // Calculate the total duration in milliseconds to perform range checks
        val totalMs = (minutes * 60 + seconds) * 1000L

        return (
                // Ensure minutes are within a 1-hour range
                minutes in 0..60 &&
                        // Ensure seconds are a valid clock value (0-59)
                        seconds in 0..59 &&
                        // Ensure total duration does not exceed 60 minutes (3,600,000 ms)
                        totalMs <= 60 * 60_000L &&
                        // Ensure the game time is actually set (cannot be 0:00)
                        totalMs > 0
                )
    }
}