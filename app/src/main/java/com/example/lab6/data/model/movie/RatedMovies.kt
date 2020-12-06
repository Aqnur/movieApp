package com.example.lab6.data.model.movie

import com.google.gson.annotations.SerializedName

data class RatedMovies (
    @SerializedName("adult") val adult : Boolean,
    @SerializedName("backdrop_path") val backdrop_path : String,
    @SerializedName("genre_ids") val genre_ids : List<Int>,
    @SerializedName("id") val id : Int,
    @SerializedName("original_language") val original_language : String,
    @SerializedName("original_title") val original_title : String,
    @SerializedName("overview") val overview : String,
    @SerializedName("release_date") val release_date : String,
    @SerializedName("poster_path") val poster_path : String,
    @SerializedName("popularity") val popularity : Double,
    @SerializedName("title") val title : String,
    @SerializedName("video") val video : Boolean,
    @SerializedName("vote_average") val vote_average : Double,
    @SerializedName("vote_count") val vote_count : Int,
    @SerializedName("rating") val rating : Int
)