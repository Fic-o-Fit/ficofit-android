package com.c22ps072.ficofit.data.source.remote.network

import com.c22ps072.ficofit.data.source.remote.response.*
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/login")
    suspend fun postUserLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @FormUrlEncoded
    @POST("/signup")
    suspend fun postUserSignUp(
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
        @Header("Cookie") cookie: String,
        @Field("score") score: Int,
    ) : ScoreResponse

    @GET("/score")
    suspend fun getAllScore(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String
    ): List<UserPoint>

    @GET("/score/me")
    suspend fun getMyScore(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String
    ): UserPoint

    @FormUrlEncoded
    @POST("/submit-weight")
    suspend fun postSubmitWeight(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Field("weight") weight: Int
    ) : GlobalResponse

    @FormUrlEncoded
    @POST("/calories-counter")
    suspend fun postCaloriesCounter(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Field("reps") reps: Int
    ) : CaloriesResponse

}