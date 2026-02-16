package com.paulnikolaus.scoreboard

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paulnikolaus.scoreboard.presentation.ScoreboardScreen
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import com.paulnikolaus.scoreboard.settings.SettingsRepository
import com.paulnikolaus.scoreboard.settings.SettingsViewModel
import com.paulnikolaus.scoreboard.ui.theme.ScoreBoardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {

            val context = LocalContext.current

            val settingsRepository = remember {
                SettingsRepository(context)
            }

            val settingsViewModel = remember {
                SettingsViewModel(settingsRepository)
            }

            val themePreference by settingsViewModel.themePreference.collectAsState()

            val systemDark = isSystemInDarkTheme()

            val finalDarkMode = themePreference ?: systemDark

            ScoreBoardTheme(
                darkTheme = finalDarkMode
            )
            {

                val scoreboardViewModel: ScoreboardViewModel =
                    viewModel()

                ScoreboardScreen(
                    viewModel = scoreboardViewModel,
                    isDarkMode = finalDarkMode,
                    onToggleDarkMode = { enabled ->
                        settingsViewModel.toggleDarkMode(enabled)
                    }
                )
            }
        }
    }
}
