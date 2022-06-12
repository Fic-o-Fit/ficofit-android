package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.remote.response.CaloriesResponse
import com.c22ps072.ficofit.data.source.remote.response.ScoreResponse
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import kotlinx.coroutines.flow.Flow

interface FicoDataSource {

    suspend fun getAllScore(token: String): Flow<Result<List<UserPoint>>>

    suspend fun getMyScore(token: String): Flow<Result<UserPoint>>

    suspend fun postSubmitScore(token: String, score: Int) : Flow<Result<ScoreResponse>>

    fun getUserName(): Flow<String>

    suspend fun saveUserName(name: String)

    fun getUserToken() : Flow<String>

    fun getUserCaloriesBurn() : Flow<Double>

    suspend fun saveUserCaloriesBurn(calories: Double)

    suspend fun postCaloriesCounter(token: String, reps: Int) : Flow<Result<CaloriesResponse>>

}