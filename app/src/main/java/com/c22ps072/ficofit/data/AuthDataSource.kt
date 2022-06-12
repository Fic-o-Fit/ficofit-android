package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.remote.response.GlobalResponse
import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    fun getUserToken(): Flow<String>

    suspend fun saveUserToken(token: String)

    fun getUserRefreshToken(): Flow<String>

    suspend fun saveUserRefreshToken(token: String)

    fun getUserEmail(): Flow<String>

    suspend fun saveUserEmail(email: String)

    fun getUserPassword() : Flow<String>

    suspend fun saveUserPassword(password: String)

    suspend fun logout()

    suspend fun postUserLogin(
        email: String,
        password: String
    ): Flow<Result<SignInResponse>>

    suspend fun postUserSignUp(
        name: String,
        email: String,
        password: String,
        gender: String,
        weight: String,
        height: String
    ): Flow<Result<SignUpResponse>>

    suspend fun postSubmitWeight(token: String, weight: Int) : Flow<Result<GlobalResponse>>
}