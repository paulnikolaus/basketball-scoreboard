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

    val isShotRunning by viewModel.isShotClockRunning.collectAsState()
    val isGameRunning by viewModel.isGameClockRunning.collectAsState()


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


    Surface {
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
                        .weight(0.8f)   // slightly narrower than score panels
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
                                Row {
                                    Button(onClick = { viewModel.toggleGameClock() }) {
                                        Text("Start / Stop")
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Button(onClick = { viewModel.resetGameClock() }) {
                                        Text("Reset")
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
                                Column {

                                    // Start / Stop on its own row
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { viewModel.toggleShotClock() }
                                    ) {
                                        Text("Start / Stop")
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // 24s / 14s share width equally
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                viewModel.resetShotClock(24)
                                            }
                                        ) {
                                            Text("24s")
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                viewModel.resetShotClock(14)
                                            }
                                        ) {
                                            Text("14s")
                                        }
                                    }
                                }
                            }
                        )

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
    }
}
