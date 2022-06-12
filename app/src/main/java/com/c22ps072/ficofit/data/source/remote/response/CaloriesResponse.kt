package com.c22ps072.ficofit.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CaloriesResponse(
    @field:SerializedName("user")
    val user: String,

    @field:SerializedName("weight")
    val weight: Int,

    @field:SerializedName("reps")
    val reps: String,

    @field:SerializedName("calories_burn")
    val caloriesBurn: Double
)