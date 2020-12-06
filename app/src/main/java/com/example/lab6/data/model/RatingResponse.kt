package com.example.lab6.data.model

import com.google.gson.annotations.SerializedName

data class RatingResponse (
    @SerializedName("success") val success : Boolean,
    @SerializedName("status_code") val status_code : Int,
    @SerializedName("status_message") val status_message : String
)