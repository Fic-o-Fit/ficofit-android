package com.c22ps072.ficofit.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22ps072.ficofit.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    /**
     * Interact with network / API
     */

    suspend fun postUserRegister(
        name: String,
        email: String,
        password: String,
        gender: String,
        weight: String,
        height: String
    ) =
        authRepository.postUserSignUp(
            name,
            email,
            password,
            gender,
            weight,
            height
        )

    suspend fun postUserSignIn(email: String, password: String) =
        authRepository.postUserLogin(email, password)

    /**
     * Interact with data store
     */

    suspend fun saveUserToken(token: String) {
        viewModelScope.launch {
            authRepository.saveUserToken(token)
        }
    }

    suspend fun saveUserRefreshToken(refreshToken: String) {
        viewModelScope.launch {
            authRepository.saveUserRefreshToken(refreshToken)
        }
    }

    suspend fun saveNameUser(name: String) {
        viewModelScope.launch {
            authRepository.saveUserName(name)
        }
    }
}