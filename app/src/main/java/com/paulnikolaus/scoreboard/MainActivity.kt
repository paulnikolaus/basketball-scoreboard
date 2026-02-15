package com.paulnikolaus.scoreboard

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.paulnikolaus.scoreboard.presentation.ScoreboardScreen
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen awake
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            ScoreboardScreen(viewModel)
        }
    }
}
