package com.c22ps072.ficofit.ui.home.leaderboard

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.AuthRepository
import com.c22ps072.ficofit.data.FicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val ficoRepo: FicoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun getUserToken() = ficoRepo.getUserToken()

    suspend fun getLeaderBoard(token: String) = ficoRepo.getAllScore(token)

    suspend fun getLeaderBoardByUser(token: String) = ficoRepo.getMyScore(token)

    suspend fun postSubmitScore(token: String, score: Int) = ficoRepo.postSubmitScore(token, score)
}