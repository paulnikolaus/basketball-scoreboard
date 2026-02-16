package com.paulnikolaus.scoreboard.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.ui.components.ScorePanel
import com.paulnikolaus.scoreboard.ui.components.ClockDisplay


@Composable
fun ScoreboardScreen(
    viewModel: ScoreboardViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {

    val score by viewModel.scoreState.collectAsState()
    val gameMs by viewModel.gameTime.collectAsState()
    val shotMs by viewModel.shotTime.collectAsState()

    val gameTimeText = run {
        val totalMs = gameMs

        if (totalMs >= 10_000L) {

            // Normal display MM:SS
            val totalSeconds = (totalMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            "%d:%02d".format(minutes, seconds)
        } else {

            // Final 10 seconds → seconds + tenths
            val seconds = totalMs / 1000
            val tenths = (totalMs % 1000) / 100

            "%.1f".format(seconds + tenths / 10f)
        }
    }


    val shotTimeText = run {
        val totalMs = shotMs

        if (totalMs >= 10_000L) {
            val seconds = (totalMs / 1000).toInt()
            "%02d".format(seconds)
        } else {
            val seconds = totalMs / 1000
            val tenths = (totalMs % 1000) / 100
            "%.1f".format(seconds + tenths / 10f)
        }
    }


    val shotButtonHeight = 48.dp

    val isShotRunning by viewModel.isShotClockRunning.collectAsState()
    val isGameRunning by viewModel.isGameClockRunning.collectAsState()

    val showGameDialog by viewModel.showGameDialog.collectAsState()
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

    val context = LocalContext.current
    val buzzerManager = remember { BuzzerManager(context) }

    val gameBuzz by viewModel.gameBuzzerEvent.collectAsState()
    val shotBuzz by viewModel.shotBuzzerEvent.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }

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
                                            minuteInput = "10"
                                            secondInput = "00"
                                            viewModel.openGameDialog()   // ✅
                                        }
                                    )
                                    {
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
                                            minuteInput = "10"
                                            secondInput = "00"
                                            viewModel.openGameDialog()   // ✅
                                        }
                                    )
                                    {
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
                    .fillMaxHeight()
            ) {

                // Settings Icon (top right)
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    onClick = { showSettingsDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }

                // AWAY Score in Center
                Box(
                    modifier = Modifier.align(Alignment.Center)
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

        //    Set time dialog
        if (showGameDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeGameDialog() },
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

                                viewModel.closeGameDialog()
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
                            viewModel.closeGameDialog()
                            minuteInput = ""
                            secondInput = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }

            )
        }

//        Settings
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("Settings") },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Dark Mode",
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { enabled ->
                                onToggleDarkMode(enabled)
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSettingsDialog = false }
                    ) {
                        Text("Close")
                    }
                }
            )
        }

    }

//    Buzzer

    LaunchedEffect(gameBuzz) {
        if (gameBuzz) {
            buzzerManager.play()
            viewModel.consumeGameBuzzer()
        }
    }

    LaunchedEffect(shotBuzz) {
        if (shotBuzz) {
            buzzerManager.play()
            viewModel.consumeShotBuzzer()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            buzzerManager.release()
        }
    }
}
