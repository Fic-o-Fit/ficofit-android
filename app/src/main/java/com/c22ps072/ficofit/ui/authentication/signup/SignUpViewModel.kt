package com.c22ps072.ficofit.ui.authentication.signup

import androidx.lifecycle.ViewModel
import com.c22ps072.ficofit.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun postUserRegister(email: String, password: String, name: String) =
        authRepository.postUserSignUp(name, email, password)
}