package com.example.lab6.model.json.account

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("username")
    var username: String = "",
    @SerializedName("session_id")
    var sessionId: String = "",
    @SerializedName("account_id")
    var accountId: Int = 9178797
)