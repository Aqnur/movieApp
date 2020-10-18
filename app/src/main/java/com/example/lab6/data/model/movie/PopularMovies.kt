package com.example.lab6.data.model.movie

import com.google.gson.annotations.SerializedName

data class PopularMovies(
    @SerializedName("results") val results: List<Result>
)

data class Genre(
    val id: Int,
    val name: String
)

data class Genres(
    val genres: List<Genre>
)