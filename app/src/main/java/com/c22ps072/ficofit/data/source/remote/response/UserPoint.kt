package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserPoint (
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("highScore")
    val highScore: Int
)