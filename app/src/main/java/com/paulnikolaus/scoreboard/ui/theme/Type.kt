package com.paulnikolaus.scoreboard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Set of Material 3 typography styles to start with.
 * This object defines the text hierarchy used throughout the Scoreboard app.
 *
 * We primarily use *bodyLarge* for standard labels and have the option to override
 * other standard Material slots like *titleLarge* and *labelSmall*.
 */
val Typography = Typography(

    // Default body text style (e.g., used for settings or general labels)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    /*
     * Potential future overrides to maintain Material Design standards:
     *
     * titleLarge = TextStyle(
     *     fontFamily = FontFamily.Default,
     *     fontWeight = FontWeight.Normal,
     *     fontSize = 22.sp,
     *     lineHeight = 28.sp,
     *     letterSpacing = 0.sp
     * ),
     *
     * labelSmall = TextStyle(
     *     fontFamily = FontFamily.Default,
     *     fontWeight = FontWeight.Medium,
     *     fontSize = 11.sp,
     *     lineHeight = 16.sp,
     *     letterSpacing = 0.5.sp
     * )
     */
)