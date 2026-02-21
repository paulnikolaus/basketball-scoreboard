package com.paulnikolaus.scoreboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A reusable UI component to display a labeled clock (Game or Shot clock)
 * along with its specific control buttons.
 *
 * @param title The label shown above the time (e.g., "GAME" or "SHOT").
 * @param timeText The formatted string representing the remaining time.
 * @param textColor The color of the time text (used for visual alerts like red when stopped).
 * @param controls A slot for Composable buttons/controls specific to this clock.
 */
@Composable
fun ClockDisplay(
    title: String,
    timeText: String,
    textColor: Color,
    controls: @Composable () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Display the label for the clock
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        // Slight spacing for a tighter visual connection between title and time
        Spacer(Modifier.height(4.dp))

        // Display the large, bold countdown numbers
        Text(
            text = timeText,
            color = textColor,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        // Spacing between the clock numbers and the action buttons
        Spacer(Modifier.height(4.dp))

        // Render the control buttons passed from the parent screen
        controls()
    }
}