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

@Composable
fun ScorePanel(
    teamName: String,
    score: Int,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit
) {
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onAdd(1) }) { Text("+1") }
            Button(onClick = { onAdd(2) }) { Text("+2") }
            Button(onClick = { onAdd(3) }) { Text("+3") }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = onUndo) {
            Text("-1")
        }

    }
}
