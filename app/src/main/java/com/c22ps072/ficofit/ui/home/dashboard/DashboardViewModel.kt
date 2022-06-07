package com.c22ps072.ficofit.ui.home.dashboard

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.FicoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val ficoRepository: FicoRepository) : ViewModel() {
    suspend fun getUserToken() = ficoRepository.getUserToken()

    suspend fun getMyScore(token: String) = ficoRepository.getMyScore(token)
}