package com.c22ps072.ficofit.data.source.remote.response

data class SignIn(
    val email: String,

    val password: String,

    val token: String,

    val refreshToken: String,
)