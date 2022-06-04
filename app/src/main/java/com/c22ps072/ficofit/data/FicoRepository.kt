package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.local.PreferenceDataStore
import com.c22ps072.ficofit.data.source.remote.network.ApiService
import com.c22ps072.ficofit.data.source.remote.response.ScoreResponse
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FicoRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: PreferenceDataStore
    ) : FicoDataSource {
    override suspend fun getAllScore(token: String): Flow<Result<List<UserPoint>>> = flow {
        try {
            val strToken = "Bearer $token"
            val response = apiService.getAllScore(strToken)
            emit(Result.success(response))
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override suspend fun getMyScore(token: String): Flow<Result<UserPoint>> = flow {
        try {
            val strToken = "Bearer $token"
            val response = apiService.getMyScore(strToken)
            emit(Result.success(response))
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override suspend fun postSubmitScore(token: String, score: Int): Flow<Result<ScoreResponse>> = flow {
        try  {
            val strToken = "Bearer $token"
            val response = apiService.postSubmitScore(strToken, score)
            emit(Result.success(response))
        } catch (err: Exception) {
            emit(Result.failure(err))
        }
    }

    override suspend fun getUserToken(): Flow<String> = dataStore.getUserToken()
}