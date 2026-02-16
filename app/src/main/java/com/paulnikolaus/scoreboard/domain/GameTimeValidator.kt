package com.paulnikolaus.scoreboard.domain

object GameTimeValidator {

    fun isValid(minutes: Int, seconds: Int): Boolean {

        val totalMs = (minutes * 60 + seconds) * 1000L

        return minutes in 0..60 &&
                seconds in 0..59 &&
                totalMs <= 60 * 60_000L &&
                totalMs > 0
    }
}
