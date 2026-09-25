package com.paulnikolaus.scoreboard

import com.paulnikolaus.scoreboard.presentation.formatGameTime
import com.paulnikolaus.scoreboard.presentation.formatShotTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

/**
 * Unit tests for the clock text formatting in TimeFormat.kt.
 * They run with a German default locale, which uses "," as its decimal separator,
 * to make sure the clocks always show "9.4" and never "9,4".
 */
class TimeFormatTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMANY)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun gameTime_aboveTenSeconds_showsMinutesAndSeconds() {
        assertEquals("10:00", formatGameTime(600_000L))
        assertEquals("1:05", formatGameTime(65_000L))
        assertEquals("0:10", formatGameTime(10_000L))
    }

    @Test
    fun gameTime_belowTenSeconds_showsTenthsWithDot() {
        assertEquals("9.4", formatGameTime(9_450L))
        assertEquals("0.0", formatGameTime(0L))
    }

    @Test
    fun shotTime_aboveTenSeconds_showsWholeSeconds() {
        assertEquals("24", formatShotTime(24_000L))
        assertEquals("10", formatShotTime(10_999L))
    }

    @Test
    fun shotTime_belowTenSeconds_showsTenthsWithDot() {
        assertEquals("4.2", formatShotTime(4_299L))
        assertEquals("0.0", formatShotTime(0L))
    }
}
