package com.example.lab6.model.json.account

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Account (
    @SerializedName("avatar")
    val avatar: Avatar,
    @SerializedName("id")
    val id: Int,
    @SerializedName("iso_639_1")
    val iso_639_1: String,
    @SerializedName("iso_3166_1")
    val iso_3166_1: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("include_adult")
    val include_adult: Boolean,
    @SerializedName("username")
    val username: String
): Serializable

data class Avatar(
    @SerializedName("gravatar")
    val gravatar: Gravatar
)
data class Gravatar(
    @SerializedName("hash")
    val hash: String
)