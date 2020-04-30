package com.example.lab6.model.json.movie

import com.google.gson.annotations.SerializedName

data class PopularMovies(
    @SerializedName("results") val results: List<Result>
)

data class Genre(
    val id: Int,
    val name: String
)

data class Validation (
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("request_token") val requestToken: String
)

data class Session(
    @SerializedName("session_id") val sessionId: String
)