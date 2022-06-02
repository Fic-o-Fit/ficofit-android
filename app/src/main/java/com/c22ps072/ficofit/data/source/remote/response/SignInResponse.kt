package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @field:SerializedName("status")
    val status : String,
    @field:SerializedName("loginResult")
    val signIn: SignIn
)

