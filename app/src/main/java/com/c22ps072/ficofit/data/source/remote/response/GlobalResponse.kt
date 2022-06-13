package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GlobalResponse(
    @field:SerializedName("message")
    val message: String
)
