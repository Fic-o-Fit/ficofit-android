package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SignIn(
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("token")
    val token: String,
    @field:SerializedName("refreshToken")
    val refreshToken: String,
)