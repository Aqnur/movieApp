package com.example.lab6.data.model.account

import com.google.gson.annotations.SerializedName

data class RequestToken(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("request_token")
    val requestToken: String
)