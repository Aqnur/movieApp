package com.example.lab6.data.model.movie

import com.google.gson.annotations.SerializedName

data class RatedMoviesResponse(
    @SerializedName("page") val page : Int,
    @SerializedName("results") val results : List<RatedMovies>,
    @SerializedName("total_pages") val total_pages : Int,
    @SerializedName("total_results") val total_results : Int
)