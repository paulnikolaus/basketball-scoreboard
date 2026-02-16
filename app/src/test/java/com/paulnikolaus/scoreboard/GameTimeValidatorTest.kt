package com.paulnikolaus.scoreboard

import com.paulnikolaus.scoreboard.domain.GameTimeValidator
import org.junit.Assert.*
import org.junit.Test

class GameTimeValidatorTest {

    @Test
    fun validTime_returnsTrue() {
        assertTrue(GameTimeValidator.isValid(10, 0))
    }

    @Test
    fun moreThan60Minutes_returnsFalse() {
        assertFalse(GameTimeValidator.isValid(61, 0))
    }

    @Test
    fun moreThan59Seconds_returnsFalse() {
        assertFalse(GameTimeValidator.isValid(10, 60))
    }

    @Test
    fun zeroTime_returnsFalse() {
        assertFalse(GameTimeValidator.isValid(0, 0))
    }
}
