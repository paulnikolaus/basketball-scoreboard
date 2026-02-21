package com.paulnikolaus.scoreboard.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Defines the color palette for Dark Mode.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Defines the color palette for Light Mode.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Additional defaults that can be overridden:
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * The main Theme wrapper for the Scoreboard application.
 *
 * @param darkTheme Boolean determining if dark mode should be applied.
 * @param dynamicColor If true, uses Android 12+ wallpaper-based colors.
 * @param content The UI content to be themed.
 */
@Composable
fun ScoreBoardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12 (API 31) and above
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine the color scheme based on system version and user preference
    val colorScheme = when {
        // Handle Dynamic Theming (Material You)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Fallback to custom defined color schemes
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply the Material 3 configuration to the composition
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Uses the Typography defined in Typography.kt
        content = content
    )
}