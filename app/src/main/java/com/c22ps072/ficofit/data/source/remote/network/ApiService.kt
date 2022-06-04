package com.c22ps072.ficofit.data.source.remote.network

import com.c22ps072.ficofit.data.source.remote.response.ScoreResponse
import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/login")
    suspend fun postUserLogin(
        @Field("Content-Type") contentType: String = "application/json",
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @FormUrlEncoded
    @POST("/signup")
    suspend fun postUserSignUp(
        @Field("Content-Type") contentType: String = "application/json",
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("gender") gender: String,
        @Field("weight") weight: String,
        @Field("height") height: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("/submit-score")
    suspend fun postSubmitScore(
        @Header("Authorization") token: String,
        @Field("score") score: Int,
    ) : ScoreResponse

    @GET("/score")
    suspend fun getAllScore(
        @Header("Authorization") token: String
    ): List<UserPoint>

    @GET("/score/me")
    suspend fun getMyScore(
        @Header("Authorization") token: String
    ): UserPoint

}