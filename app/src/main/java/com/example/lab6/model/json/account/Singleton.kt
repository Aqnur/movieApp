package com.example.lab6.model.json.account

import com.google.gson.annotations.SerializedName

class Singleton(
    @SerializedName("username")
    var username: String,
    @SerializedName("session_id")
    var sessionId: String,
    @SerializedName("account_id")
    var accountId: Int
) {

    companion object Factory {
        @Volatile
        private var INSTANCE: Singleton? = null

        @Synchronized
        fun create(username: String, session_id: String, account_id: Int):
                Singleton =
            INSTANCE
                ?: Singleton(
                    username,
                    session_id,
                    account_id
                )
                    .also { INSTANCE = it }

        fun getSession(): String {
            return INSTANCE!!.sessionId
        }

        fun getAccountId(): Int {
            return INSTANCE!!.accountId
        }

        fun getUserName(): String {
            return INSTANCE!!.username
        }

    }
}