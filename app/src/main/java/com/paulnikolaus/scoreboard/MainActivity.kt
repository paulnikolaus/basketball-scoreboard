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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
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
            // Obtain the current context for repository initialization
            val context = LocalContext.current

            // Initialize the SettingsRepository using 'remember' to persist across recompositions
            val settingsRepository = remember {
                SettingsRepository(context)
            }

            // Initialize SettingsViewModel to manage app-wide preferences like themes
            val settingsViewModel = remember {
                SettingsViewModel(settingsRepository)
            }

            // Observe the theme preference from DataStore/SharedPreferences as state
            val themePreference by settingsViewModel.themePreference.collectAsState()

            // Check the system-wide dark mode setting
            val systemDark = isSystemInDarkTheme()

            // Determine the final theme: use the user preference if set, otherwise fallback to system setting
            val finalDarkMode = themePreference ?: systemDark

            // Apply the custom Material3 theme to the application
            ScoreBoardTheme(
                darkTheme = finalDarkMode
            ) {
                // Initialize the main Scoreboard ViewModel
                val scoreboardViewModel: ScoreboardViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(
                            modelClass: Class<T>,
                            extras: androidx.lifecycle.viewmodel.CreationExtras
                        ): T {
                            // Obtain the SavedStateHandle from the CreationExtras
                            val handle = extras.createSavedStateHandle()

                            // Manually create the ViewModel and inject the SavedStateHandle.
                            // The time providers will use their default values automatically.
                            return ScoreboardViewModel(handle) as T
                        }
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