package com.c22ps072.ficofit.ui.home.leaderboard

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.FicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val ficoRepo: FicoRepository
) : ViewModel() {
    fun getUserToken() = ficoRepo.getUserToken()

    suspend fun getLeaderBoard(token: String) = ficoRepo.getAllScore(token)
}