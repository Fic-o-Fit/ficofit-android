package com.c22ps072.ficofit.data.source.remote.network

import com.c22ps072.ficofit.data.source.remote.response.SignInResponse
import com.c22ps072.ficofit.data.source.remote.response.SignUpResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
        @Field("password") password: String
    ): SignUpResponse
}