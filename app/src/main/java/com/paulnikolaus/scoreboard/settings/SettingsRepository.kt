package com.paulnikolaus.scoreboard.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension property to create a DataStore instance.
 * DataStore is the modern replacement for SharedPreferences,
 * offering better performance and Coroutine support.
 */
private val Context.dataStore by preferencesDataStore("settings")

/**
 * Repository class responsible for persisting and retrieving application settings.
 * Currently manages the user's Dark Mode preference.
 */
class SettingsRepository(private val context: Context) {

    companion object {
        /**
         * Key used to store the boolean value for dark mode in the DataStore.
         */
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    /**
     * A [Flow] that emits the current Dark Mode preference.
     * Returns:
     * - 'true' if dark mode is enabled
     * - 'false' if light mode is enabled
     * - 'null' if the user has never set a preference (fallback to system setting)
     */
    val isDarkMode: Flow<Boolean?> =
        context.dataStore.data.map { preferences ->
            // Access the value using our typed key
            preferences[DARK_MODE]
        }

    /**
     * Updates and persists the user's Dark Mode preference.
     *
     * @param enabled True for Dark Mode, False for Light Mode.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            // Update the value in the DataStore
            preferences[DARK_MODE] = enabled
        }
    }
}