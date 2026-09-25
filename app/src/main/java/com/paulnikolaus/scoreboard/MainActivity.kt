package com.paulnikolaus.scoreboard

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.paulnikolaus.scoreboard.presentation.ScoreboardScreen
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import com.paulnikolaus.scoreboard.settings.SettingsRepository
import com.paulnikolaus.scoreboard.settings.SettingsViewModel
import com.paulnikolaus.scoreboard.ui.theme.ScoreBoardTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep the screen on while the app is in the foreground (useful for a scoreboard)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            // Obtain SettingsViewModel through viewModel() so it survives screen rotation
            // and is properly cleared when the Activity finishes.
            // The repository gets the applicationContext because it may outlive this Activity.
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { SettingsViewModel(SettingsRepository(applicationContext)) }
                }
            )

            // Observe the theme preference from DataStore as state
            val themePreference by settingsViewModel.themePreference.collectAsState()

            // Check the system-wide dark mode setting
            val systemDark = isSystemInDarkTheme()

            // Determine the final theme: use the user preference if set, otherwise fallback to system setting
            val finalDarkMode = themePreference ?: systemDark

            // Apply the custom Material3 theme to the application
            ScoreBoardTheme(
                darkTheme = finalDarkMode
            ) {
                // Initialize the main Scoreboard ViewModel and inject its SavedStateHandle.
                // The time providers will use their default values automatically.
                val scoreboardViewModel: ScoreboardViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { ScoreboardViewModel(createSavedStateHandle()) }
                    }
                )

                // Display the main UI screen
                ScoreboardScreen(
                    viewModel = scoreboardViewModel,
                    isDarkMode = finalDarkMode,
                    onToggleDarkMode = { enabled ->
                        // Callback to update the theme preference when toggled in the UI
                        settingsViewModel.toggleDarkMode(enabled)
                    }
                )
            }
        }
    }
}
