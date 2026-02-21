package com.paulnikolaus.scoreboard.data

import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel

/** * Represents the two competing teams in a basketball game.
 * Used to identify which side is receiving points or whose score
 * is being modified in the [ScoreboardViewModel].
 */
enum class Team {
    /** The home team (usually the primary team for the user). */
    HOME,

    /** The visiting or opposing team. */
    AWAY
}