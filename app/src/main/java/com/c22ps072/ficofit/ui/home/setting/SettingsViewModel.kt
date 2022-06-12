package com.c22ps072.ficofit.ui.home.setting

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    suspend fun logout() = authRepository.logout()
}