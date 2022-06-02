package com.c22ps072.ficofit.data

import com.c22ps072.ficofit.data.source.remote.response.ScoreResponse
import kotlinx.coroutines.flow.Flow

interface FicoDataSource {
    suspend fun postSubmitScore() : Flow<Result<ScoreResponse>>

}