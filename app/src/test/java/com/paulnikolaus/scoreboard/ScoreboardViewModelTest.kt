package com.paulnikolaus.scoreboard

import androidx.lifecycle.SavedStateHandle
import com.paulnikolaus.scoreboard.data.Team
import com.paulnikolaus.scoreboard.presentation.ScoreboardViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ScoreboardViewModelTest {

    private lateinit var viewModel: ScoreboardViewModel

    @Before
    fun setup() {
        val savedStateHandle = SavedStateHandle()
        viewModel = ScoreboardViewModel(savedStateHandle)
    }

    @Test
    fun addScore_home_increasesScoreCorrectly() {

        viewModel.addScore(Team.HOME, 2)

        val score = viewModel.scoreState.value

        assertEquals(2, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun undoScore_doesNotGoBelowZero() {

        viewModel.undoScore(Team.HOME)

        val score = viewModel.scoreState.value

        assertEquals(0, score.home)
    }

    @Test
    fun resetScores_setsBothToZero() {

        viewModel.addScore(Team.HOME, 5)
        viewModel.addScore(Team.AWAY, 3)

        viewModel.resetScores()

        val score = viewModel.scoreState.value

        assertEquals(0, score.home)
        assertEquals(0, score.away)
    }

    @Test
    fun multipleAddScore_accumulatesCorrectly() {

        viewModel.addScore(Team.HOME, 2)
        viewModel.addScore(Team.HOME, 3)

        assertEquals(5, viewModel.scoreState.value.home)
    }


}
