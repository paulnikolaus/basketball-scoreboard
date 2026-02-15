package com.paulnikolaus.scoreboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.ui.components.ScorePanel
import com.paulnikolaus.scoreboard.ui.components.ClockDisplay


@Composable
fun ScoreboardScreen(viewModel: ScoreboardViewModel) {

    val score by viewModel.scoreState.collectAsState()
    val gameMs by viewModel.gameTime.collectAsState()
    val shotMs by viewModel.shotTime.collectAsState()

    val gameTimeText = run {
        val totalSeconds = (gameMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        "%d:%02d".format(minutes, seconds)
    }

    val shotTimeText = run {
        val seconds = (shotMs / 1000).toInt()
        "%02d".format(seconds)
    }

    val shotButtonHeight = 48.dp

    val isShotRunning by viewModel.isShotClockRunning.collectAsState()
    val isGameRunning by viewModel.isGameClockRunning.collectAsState()

    var showGameDialog by remember { mutableStateOf(false) }
    var minuteInput by remember { mutableStateOf("") }
    var secondInput by remember { mutableStateOf("") }

    val shotColor =
        if (isShotRunning)
            Color.Green
        else
            Color.Red

    val gameColor =
        if (isGameRunning)
            MaterialTheme.colorScheme.onSurface
        else
            Color.Red

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    Surface {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🔵 HOME
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                ScorePanel(
                    teamName = "HOME",
                    score = score.home,
                    onAdd = { viewModel.addScore(Team.HOME, it) },
                    onUndo = { viewModel.undoScore(Team.HOME) }
                )
            }

            // 🟢 CLOCKS
            Box(
                modifier = Modifier
                    .weight(1.4f)   // ← slightly bigger
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {

                    ClockDisplay(
                        title = "GAME",
                        timeText = gameTimeText,
                        textColor = gameColor,
                        controls = {
                            if (isLandscape) {
                                // Landscape → horizontal
                                Row {
                                    Button(onClick = { viewModel.toggleGameClock() }) {
                                        Text("Start / Stop")
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                             // Pre-fill dialog with current value
                                            minuteInput = 10.toString()
                                            secondInput = 0.toString().padStart(2, '0')

                                            showGameDialog = true
                                        }
                                    ) {
                                        Text("Set Time")
                                    }

                                }

                            } else {
                                // Portrait → vertical
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { viewModel.toggleGameClock() }
                                    ) {
                                        Text("Start / Stop")
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            // Pre-fill dialog with current value
                                            minuteInput = 10.toString()
                                            secondInput = 0.toString().padStart(2, '0')

                                            showGameDialog = true
                                        }
                                    ) {
                                        Text("Set Time")
                                    }
                                }
                            }

                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    ClockDisplay(
                        title = "SHOT",
                        timeText = shotTimeText,
                        textColor = shotColor,
                        controls = {

                            if (isLandscape) {

                                // 🖥 Landscape → all in one row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(shotButtonHeight),
                                        onClick = { viewModel.toggleShotClock() }
                                    )
                                    {
                                        Text("Start / Stop")
                                    }

                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(shotButtonHeight),
                                        onClick = { viewModel.resetShotClock(24) }
                                    ) {
                                        Text("24s")
                                    }

                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(shotButtonHeight),
                                        onClick = { viewModel.resetShotClock(14) }
                                    ) {
                                        Text("14s")
                                    }
                                }

                            } else {

                                // 📱 Portrait
                                Column {

                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(shotButtonHeight),
                                        onClick = { viewModel.toggleShotClock() }
                                    ) {
                                        Text("Start / Stop")
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .height(shotButtonHeight),
                                    ) {

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = { viewModel.resetShotClock(24) }
                                        ) {
                                            Text("24s")
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = { viewModel.resetShotClock(14) }
                                        ) {
                                            Text("14s")
                                        }
                                    }
                                }
                            }
                        }

                    )

//                        Show this button only in portrait mode
                    if (!isLandscape) {
                        Spacer(Modifier.height(32.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            onClick = { viewModel.resetScores() }
                        ) {
                            Text("RESET SCORE")
                        }
                    }

                }
            }

            // 🔴 AWAY
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                ScorePanel(
                    teamName = "AWAY",
                    score = score.away,
                    onAdd = { viewModel.addScore(Team.AWAY, it) },
                    onUndo = { viewModel.undoScore(Team.AWAY) }
                )
            }
        }
    }

    if (showGameDialog) {
        AlertDialog(
            onDismissRequest = { showGameDialog = false },
            title = { Text("Set Game Time (MM : SS)") },
            text = {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        modifier = Modifier.width(100.dp),
                        value = minuteInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {

                                val numericValue = newValue.toIntOrNull()

                                if (numericValue == null || numericValue <= 60) {
                                    minuteInput = newValue
                                }
                            }
                        },
                        singleLine = true,
                        label = { Text("MM") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Text(
                        ":",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    OutlinedTextField(
                        modifier = Modifier.width(100.dp),
                        value = secondInput,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 2) {

                                val numericValue = newValue.toIntOrNull()

                                if (numericValue == null || numericValue <= 59) {
                                    secondInput = newValue
                                }
                            }
                        },
                        singleLine = true,
                        label = { Text("SS") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {

                        val minutes = minuteInput.toIntOrNull() ?: 0
                        val seconds = secondInput.toIntOrNull() ?: 0

                        val totalMs = (minutes * 60 + seconds) * 1000L

                        if (
                            minutes in 0..60 &&
                            seconds in 0..59 &&
                            totalMs <= 60 * 60_000L &&
                            totalMs > 0
                        ) {
                            viewModel.setGameDuration(minutes, seconds)

                            showGameDialog = false
                            minuteInput = ""
                            secondInput = ""
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showGameDialog = false
                        minuteInput = ""
                        secondInput = ""
                    }
                ) {
                    Text("Cancel")
                }
            }

        )
    }

}
