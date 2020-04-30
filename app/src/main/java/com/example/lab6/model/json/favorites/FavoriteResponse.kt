package com.example.lab6.model.json.favorites

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("status_code") val statusCode:Int,
    @SerializedName("status_message") val statusMessage:String
)