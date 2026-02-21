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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.ui.components.ScorePanel
import com.paulnikolaus.scoreboard.ui.components.ClockDisplay
import com.paulnikolaus.scoreboard.ui.theme.ForestGreen

/**
 * The main UI screen for the Scoreboard application.
 *
 * This Composable is "State-Aware." It observes data flows from the [ScoreboardViewModel]
 * and re-renders only the specific components that change (e.g., just the clock numbers).
 */
@Composable
fun ScoreboardScreen(
    viewModel: ScoreboardViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    // --- STATE OBSERVATION ---
    // .collectAsState() converts Kotlin Flows into Compose State.
    // Whenever the score or time changes in the ViewModel, these variables trigger a UI refresh.
    val score by viewModel.scoreState.collectAsState()
    val gameMs by viewModel.gameTime.collectAsState()
    val shotMs by viewModel.shotTime.collectAsState()

    // --- DYNAMIC TIME FORMATTING ---

    /**
     * Logic for the Game Timer:
     * Displays Minutes:Seconds (e.g., 10:00) until the clock hits 10 seconds.
     * Below 10 seconds, it switches to "Tenths Mode" (e.g., 9.4) for high-stakes accuracy.
     */
    val gameTimeText = run {
        val totalMs = gameMs
        if (totalMs >= 10_000L) {
            val totalSeconds = (totalMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            "%d:%02d".format(minutes, seconds)
        } else {
            val seconds = totalMs / 1000
            val tenths = (totalMs % 1000) / 100
            "%.1f".format(seconds + tenths / 10f)
        }
    }

    /**
     * Logic for the Shot Clock:
     * Usually 24 or 14. Also switches to tenths (e.g., 4.2) when time is running out.
     */
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

    // UI Constants
    val shotButtonHeight = 48.dp
    val isShotRunning by viewModel.isShotClockRunning.collectAsState()
    val isGameRunning by viewModel.isGameClockRunning.collectAsState()
    val showGameDialog by viewModel.showGameDialog.collectAsState()

    // --- DIALOG INPUT STATE ---
    // These are local to the UI because they are only used while the user is typing.
    var minuteInput by remember { mutableStateOf("") }
    var secondInput by remember { mutableStateOf("") }


    // --- VISUAL FEEDBACK COLORS ---
    // We use ForestGreen (defined in Color.kt) for a more professional look.
    // The Game Clock turns red when paused to signal to the officials/players that it's stopped.
    val shotColor = if (isShotRunning) ForestGreen else Color.Red
    val gameColor = if (isGameRunning) MaterialTheme.colorScheme.onSurface else Color.Red

    // --- ADAPTIVE LAYOUT DETECTION ---
    // Detects if the phone is sideways (Landscape) or upright (Portrait).
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // --- AUDIO SETUP ---
    // BuzzerManager is 'remembered' so it isn't recreated every time a number changes.
    val context = LocalContext.current
    val buzzerManager = remember { BuzzerManager(context) }
    val gameBuzz by viewModel.gameBuzzerEvent.collectAsState()
    val shotBuzz by viewModel.shotBuzzerEvent.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    Surface {
        // Main structural layout: Left (Home), Center (Clocks), Right (Away)
        Row(modifier = Modifier.fillMaxSize()) {

            // 🔵 HOME TEAM PANEL
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                ScorePanel(
                    teamName = "HOME",
                    score = score.home,
                    onAdd = { viewModel.addScore(Team.HOME, it) },
                    onUndo = { viewModel.undoScore(Team.HOME) }
                )
            }

            // 🟢 CENTER SECTION: CLOCKS & CONTROLS
            // This section is allocated more width (1.4f weight) to prioritize the timer.
            Box(
                modifier = Modifier.weight(1.4f).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    // verticalScroll allows small screens to scroll if the buttons don't fit
                    modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // --- Game Clock UI ---
                    ClockDisplay(
                        title = "GAME",
                        timeText = gameTimeText,
                        textColor = gameColor,
                        controls = {
                            if (isLandscape) {
                                // Side-by-side buttons for wide screens
                                Row {
                                    Button(onClick = { viewModel.toggleGameClock() }) { Text("Start / Stop") }
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = {
                                        minuteInput = "10"; secondInput = "00"
                                        viewModel.openGameDialog()
                                    }) { Text("Set Time") }
                                }
                            } else {
                                // Stacked buttons for narrow screens
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.toggleGameClock() }) { Text("Start / Stop") }
                                    Spacer(Modifier.height(8.dp))
                                    Button(onClick = {
                                        minuteInput = "10"; secondInput = "00"
                                        viewModel.openGameDialog()
                                    }) { Text("Set Time") }
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    // --- Shot Clock UI ---
                    ClockDisplay(
                        title = "SHOT",
                        timeText = shotTimeText,
                        textColor = shotColor,
                        controls = {
                            if (isLandscape) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(modifier = Modifier.weight(1f).height(shotButtonHeight), onClick = { viewModel.toggleShotClock() }) { Text("Start / Stop") }
                                    Button(modifier = Modifier.weight(1f).height(shotButtonHeight), onClick = { viewModel.resetShotClock(24) }) { Text("24s") }
                                    Button(modifier = Modifier.weight(1f).height(shotButtonHeight), onClick = { viewModel.resetShotClock(14) }) { Text("14s") }
                                }
                            } else {
                                Column {
                                    Button(modifier = Modifier.fillMaxWidth().height(shotButtonHeight), onClick = { viewModel.toggleShotClock() }) { Text("Start / Stop") }
                                    Spacer(Modifier.height(8.dp))
                                    Row(modifier = Modifier.height(shotButtonHeight)) {
                                        Button(modifier = Modifier.weight(1f), onClick = { viewModel.resetShotClock(24) }) { Text("24s") }
                                        Spacer(Modifier.width(8.dp))
                                        Button(modifier = Modifier.weight(1f), onClick = { viewModel.resetShotClock(14) }) { Text("14s") }
                                    }
                                }
                            }
                        }
                    )

                    // Extra spacing and Global Reset button (Only shown in Portrait to avoid clutter)
                    if (!isLandscape) {
                        Spacer(Modifier.height(32.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = { viewModel.resetScores() }
                        ) { Text("RESET SCORE") }
                    }
                }
            }

            // 🔴 AWAY TEAM PANEL & SETTINGS
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                // Settings Icon positioned in the corner, safe from system status bars
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp),
                    onClick = { showSettingsDialog = true }
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }

                Box(modifier = Modifier.align(Alignment.Center)) {
                    ScorePanel(
                        teamName = "AWAY",
                        score = score.away,
                        onAdd = { viewModel.addScore(Team.AWAY, it) },
                        onUndo = { viewModel.undoScore(Team.AWAY) }
                    )
                }
            }
        }

        // --- OVERLAY DIALOGS ---

        /**
         * "Set Game Time" Dialog:
         * Uses specific keyboard options for numeric entry.
         * Validates digits only and limits length to 2 characters per field.
         */
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
                            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) minuteInput = it },
                            singleLine = true,
                            label = { Text("MM") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(":", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            modifier = Modifier.width(100.dp),
                            value = secondInput,
                            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) secondInput = it },
                            singleLine = true,
                            label = { Text("SS") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val minutes = minuteInput.toIntOrNull() ?: 0
                        val seconds = secondInput.toIntOrNull() ?: 0
                        // Logic Check: only close if the time is valid (e.g., seconds < 60)
                        if (viewModel.setGameTimeIfValid(minutes, seconds)) {
                            viewModel.closeGameDialog()
                            minuteInput = ""; secondInput = ""
                        }
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeGameDialog(); minuteInput = ""; secondInput = "" }) { Text("Cancel") }
                }
            )
        }

        /**
         * "Settings" Dialog:
         * Contains the switch for Light/Dark mode.
         */
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("Settings") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Dark Mode", modifier = Modifier.weight(1f))
                        Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode(it) })
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) { Text("Close") }
                }
            )
        }
    }

    // --- SIDE EFFECTS (NON-UI LOGIC) ---

    // Effect: Play buzzer sound exactly once when the game timer event is triggered.
    LaunchedEffect(gameBuzz) {
        if (gameBuzz) {
            buzzerManager.play()
            viewModel.consumeGameBuzzer() // Tells the ViewModel the sound was played
        }
    }

    // Effect: Play buzzer sound for the shot clock expiration.
    LaunchedEffect(shotBuzz) {
        if (shotBuzz) {
            buzzerManager.play()
            viewModel.consumeShotBuzzer()
        }
    }

    // Effect: Cleanup. When this screen is removed from the screen, release the audio memory.
    DisposableEffect(Unit) {
        onDispose {
            buzzerManager.release()
        }
    }
}
