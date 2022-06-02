package com.c22ps072.ficofit.ui.authentication.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22ps072.ficofit.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun postUserSignIn(email: String, password: String) =
        authRepository.postUserLogin(email, password)

    suspend fun saveUserToken(token: String) {
        viewModelScope.launch {
            authRepository.saveUserToken(token)
        }
    }

    suspend fun saveNameUser(name: String) {
        viewModelScope.launch {
            authRepository.saveUserName(name)
        }
    }
}