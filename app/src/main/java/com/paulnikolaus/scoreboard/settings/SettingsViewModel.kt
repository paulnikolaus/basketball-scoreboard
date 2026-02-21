package com.paulnikolaus.scoreboard.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing application-wide settings,
 * specifically the theme (Dark/Light mode) preference.
 */
class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    /**
     * Converts the *repository.isDarkMode* Flow into a [StateFlow].
     *
     * - [SharingStarted.WhileSubscribed(5000)]: Keeps the flow active for 5 seconds after
     *   the last UI observer disconnects. This prevents unnecessary restarts during
     *   configuration changes (like screen rotation).
     * - initialValue = null: Represents that the user hasn't explicitly set a
     *   preference yet (allowing the app to fall back to system settings).
     */
    val themePreference =
        repository.isDarkMode.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    /**
     * Updates the user's dark mode preference asynchronously.
     *
     * @param enabled True to enable Dark Mode, False to enable Light Mode.
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }
}
