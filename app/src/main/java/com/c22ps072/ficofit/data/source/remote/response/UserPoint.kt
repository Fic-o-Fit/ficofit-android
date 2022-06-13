package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserPoint (
    @field:SerializedName("position")
    val position: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("score")
    val score: Int
)