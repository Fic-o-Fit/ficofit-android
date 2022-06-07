package com.c22ps072.ficofit.ui.splash

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    fun getUserToken() = authRepository.getUserToken()
}