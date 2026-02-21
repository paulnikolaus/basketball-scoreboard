package com.paulnikolaus.scoreboard.ui.components

import androidx.compose.foundation.layout.*import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration

/**
 * A UI component representing a team's scoring area.
 * Displays the team name, their current score, and a set of buttons
 * to increment or decrement the score.
 *
 * @param teamName The name of the team (e.g., "HOME" or "AWAY").
 * @param score The current point total for the team.
 * @param onAdd Callback triggered when a point button (+1, +2, +3) is pressed.
 * @param onUndo Callback triggered when the decrement button (-1) is pressed.
 */
@Composable
fun ScorePanel(
    teamName: String,
    score: Int,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit
) {
    // Check orientation to optimize the button layout
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Team Label
        Text(text = teamName)

        Spacer(Modifier.height(16.dp))

        // Large Score Display
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(16.dp))

        if (isLandscape) {
            /**
             * LANDSCAPE LAYOUT:
             * Uses a 2x2 grid approach to save vertical space.
             * Left Column: +1, +3
             * Right Column: +2, -1
             */
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { onAdd(1) }) { Text("+1") }
                    Button(onClick = { onAdd(3) }) { Text("+3") }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { onAdd(2) }) { Text("+2") }
                    Button(onClick = onUndo) { Text("-1") }
                }
            }
        } else {
            /**
             * PORTRAIT LAYOUT:
             * Stacked vertically in a single column for better reachability
             * on narrow screens.
             */
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { onAdd(1) }) { Text("+1") }
                Button(onClick = { onAdd(2) }) { Text("+2") }
                Button(onClick = { onAdd(3) }) { Text("+3") }
                Button(onClick = onUndo) { Text("-1") }
            }
        }
    }
}