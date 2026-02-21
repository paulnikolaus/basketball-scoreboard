package com.paulnikolaus.scoreboard.data

/** * Immutable data class representing the current point totals for a game.
 *
 * @property home The current score for the Home team. Defaults to 0.
 * @property away The current score for the Away team. Defaults to 0.
 */
data class ScoreState(
    val home: Int = 0,
    val away: Int = 0
)