package com.c22ps072.ficofit.data


import android.util.Log
import com.c22ps072.ficofit.data.source.local.PreferenceDataStore
import com.c22ps072.ficofit.data.source.remote.network.ApiService
import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor (
    private val dataStore: PreferenceDataStore,
    private val apiService: ApiService
): AuthDataSource {
    override fun getUserToken(): Flow<String> = dataStore.getUserToken()

    override suspend fun saveUserToken(token: String) = dataStore.saveUserToken(token)

    override fun getUserRefreshToken(): Flow<String> = dataStore.getUserRefreshToken()

    override suspend fun saveUserRefreshToken(token: String) = dataStore.saveUserRefreshToken(token)

    override fun getUserEmail(): Flow<String> = dataStore.getUserEmail()

    override suspend fun saveUserEmail(email: String) = dataStore.saveUserEmail(email)

    override fun getUserPassword(): Flow<String> = dataStore.getUserPassword()

    override suspend fun saveUserPassword(password: String) = dataStore.saveUserPassword(password)

    override suspend fun logout() = dataStore.clearCache()

    override suspend fun postUserLogin(email: String, password: String): Flow<Result<SignInResponse>> = flow {
        try {
            val response = apiService.postUserLogin(email=email, password=password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.message?.let { Log.e("Login", it) }
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun postUserSignUp(
        name: String,
        email: String,
        password: String,
        gender: String,
        weight: String,
        height: String
    ): Flow<Result<SignUpResponse>> = flow {
        try {
            val response = apiService.postUserSignUp(
                name=name,
                email=email,
                password=password,
                gender=gender,
                weight=weight,
                height=height
            )
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}