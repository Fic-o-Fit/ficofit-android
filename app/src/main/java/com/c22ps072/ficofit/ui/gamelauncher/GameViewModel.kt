package com.c22ps072.ficofit.ui.gamelauncher

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.FicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val ficoRepository: FicoRepository) : ViewModel() {
    suspend fun postSubmitScore(token: String, score: Int) = ficoRepository.postSubmitScore(token, score)
    fun getUserToken() = ficoRepository.getUserToken()
 }