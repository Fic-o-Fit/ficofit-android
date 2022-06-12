package com.c22ps072.ficofit.ui.gamelauncher

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.FicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val ficoRepository: FicoRepository) : ViewModel() {
    suspend fun postSubmitScore(token: String, score: Int) = ficoRepository.postSubmitScore(token, score)
    fun getUserToken() = ficoRepository.getUserToken()
    suspend fun getMyScore(token: String) = ficoRepository.getMyScore(token)
    suspend fun postCaloriesCounter(token: String, reps: Int) = ficoRepository.postCaloriesCounter(token, reps)
    fun getUserCalories() = ficoRepository.getUserCaloriesBurn()
    suspend fun saveUserCalories(calories: Double) = ficoRepository.saveUserCaloriesBurn(calories)
 }