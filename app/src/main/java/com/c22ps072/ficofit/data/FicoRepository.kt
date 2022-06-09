package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.local.PreferenceDataStore
import com.c22ps072.ficofit.data.source.remote.network.ApiService
import com.c22ps072.ficofit.data.source.remote.response.ScoreResponse
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URLEncoder
import javax.inject.Inject

class FicoRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: PreferenceDataStore
    ) : FicoDataSource {
    private fun setCookie(email: String, jwt: String) : String {
        val urlEncodedEmail = URLEncoder.encode(email, "UTF-8")
        return "Content-Type=application%2Fjson; emailSession=$urlEncodedEmail; jwt=$jwt"
    }

    override suspend fun getAllScore(token: String): Flow<Result<List<UserPoint>>> = flow {
        try {
            val strToken = "Bearer $token"
            dataStore.getUserEmail().collect {
                val response = apiService.getAllScore(strToken, setCookie(it, token))
                emit(Result.success(response))
            }
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override suspend fun getMyScore(token: String): Flow<Result<UserPoint>> = flow {
        try {
            val strToken = "Bearer $token"
            dataStore.getUserEmail().collect {
                val response = apiService.getMyScore(strToken, setCookie(it, token))
                emit(Result.success(response))
            }
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override suspend fun postSubmitScore(token: String, score: Int): Flow<Result<ScoreResponse>> = flow {
        try  {
            val strToken = "Bearer $token"
            dataStore.getUserEmail().collect {
                val response = apiService.postSubmitScore(strToken, setCookie(it, token), score)
                emit(Result.success(response))
            }
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override fun getUserName(): Flow<String> = dataStore.getUserName()

    override suspend fun saveUserName(name: String) = dataStore.saveUserName(name)

    override  fun getUserToken(): Flow<String> = dataStore.getUserToken()
}