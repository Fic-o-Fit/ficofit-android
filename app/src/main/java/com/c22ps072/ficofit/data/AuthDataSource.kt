package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import kotlinx.coroutines.flow.Flow

interface AuthDataSource {
    fun getUserToken(): Flow<String>

    suspend fun saveUserToken(token: String)

    fun getUserRefreshToken(): Flow<String>

    suspend fun saveUserRefreshToken(token: String)

    fun getUserName(): Flow<String>

    suspend fun saveUserName(name: String)

    fun postUserLogin(
        email: String,
        password: String
    ): Flow<Result<SignInResponse>>

    fun postUserSignUp(
        name: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>>
}