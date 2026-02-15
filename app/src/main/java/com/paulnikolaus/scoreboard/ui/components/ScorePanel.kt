package com.paulnikolaus.scoreboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration


@Composable
fun ScorePanel(
    teamName: String,
    score: Int,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = teamName)

        Spacer(Modifier.height(16.dp))

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(16.dp))

        if (isLandscape) {
            // 2 columns in landscape
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
            // Portrait → stacked vertically
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

