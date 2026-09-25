package com.paulnikolaus.scoreboard.presentation

import java.util.Locale

/**
 * Formats the Game Clock:
 * Displays Minutes:Seconds (e.g., 10:00) until the clock hits 10 seconds.
 * Below 10 seconds, it switches to "Tenths Mode" (e.g., 9.4) for high-stakes accuracy.
 */
internal fun formatGameTime(totalMs: Long): String =
    if (totalMs >= 10_000L) {
        val totalSeconds = (totalMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        "%d:%02d".format(Locale.ROOT, minutes, seconds)
    } else {
        formatTenths(totalMs)
    }

/**
 * Formats the Shot Clock:
 * Usually 24 or 14. Also switches to tenths (e.g., 4.2) when time is running out.
 */
internal fun formatShotTime(totalMs: Long): String =
    if (totalMs >= 10_000L) {
        val seconds = (totalMs / 1000).toInt()
        "%02d".format(Locale.ROOT, seconds)
    } else {
        formatTenths(totalMs)
    }

/**
 * Formats a time below 10 seconds as seconds and tenths (e.g., 9450 ms -> "9.4").
 *
 * Uses integer math and [Locale.ROOT] so the separator is always a dot,
 * regardless of the device language (German would otherwise show "9,4").
 */
private fun formatTenths(totalMs: Long): String {
    val seconds = totalMs / 1000
    val tenths = (totalMs % 1000) / 100
    return "%d.%d".format(Locale.ROOT, seconds, tenths)
}
