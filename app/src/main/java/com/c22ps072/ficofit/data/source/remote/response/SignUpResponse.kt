package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse (
    @field:SerializedName("status")
    val status: String
        )