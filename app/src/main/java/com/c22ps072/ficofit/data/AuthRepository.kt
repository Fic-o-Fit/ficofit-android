package com.c22ps072.ficofit.data


import com.c22ps072.ficofit.data.source.local.PreferenceDataStore
import com.c22ps072.ficofit.data.source.remote.network.ApiService
import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepository(
    private val dataStore: PreferenceDataStore,
    private val apiService: ApiService
): AuthDataSource {
    override fun getUserToken(): Flow<String> = dataStore.getUserToken()

    override suspend fun saveUserToken(token: String) = dataStore.saveUserToken(token)

    override fun getUserRefreshToken(): Flow<String> = dataStore.getUserRefreshToken()

    override suspend fun saveUserRefreshToken(token: String) = dataStore.saveUserRefreshToken(token)

    override fun getUserName(): Flow<String> = dataStore.getUserName()

    override suspend fun saveUserName(name: String) = dataStore.saveUserName(name)

    override fun postUserLogin(email: String, password: String): Flow<Result<SignInResponse>> = flow {
        try {
            val response = apiService.postUserLogin(email=email, password=password)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun postUserSignUp(
        name: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>> = flow {
        try {
            val response = apiService.postUserSignUp(name=name, email=email, password=password)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}