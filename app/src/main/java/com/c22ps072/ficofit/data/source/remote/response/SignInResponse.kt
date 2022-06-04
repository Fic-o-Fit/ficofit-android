package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @field:SerializedName("message")
    val message : String,
    @field:SerializedName("token")
    val token: String,
    @field:SerializedName("refreshToken")
    val refreshToken: String,
)

