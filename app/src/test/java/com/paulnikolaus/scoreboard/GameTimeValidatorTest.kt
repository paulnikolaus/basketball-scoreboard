package com.paulnikolaus.scoreboard

import com.paulnikolaus.scoreboard.domain.GameTimeValidator
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the [GameTimeValidator] logic.
 * These tests ensure that the game duration input by a user follows
 * expected constraints (e.g., valid minute/second ranges).
 */
class GameTimeValidatorTest {

    @Test
    fun validTime_returnsTrue() {
        // Verifies that a standard time (10 minutes, 0 seconds) is accepted
        assertTrue(GameTimeValidator.isValid(10, 0))
    }

    @Test
    fun moreThan60Minutes_returnsFalse() {
        // Verifies that the app rejects durations longer than 60 minutes
        assertFalse(GameTimeValidator.isValid(61, 0))
    }

    @Test
    fun moreThan59Seconds_returnsFalse() {
        // Verifies that the seconds field must be a valid clock value (0-59)
        assertFalse(GameTimeValidator.isValid(10, 60))
    }

    @Test
    fun zeroTime_returnsFalse() {
        // Verifies that a game cannot be started with a duration of 0:00
        assertFalse(GameTimeValidator.isValid(0, 0))
    }
}