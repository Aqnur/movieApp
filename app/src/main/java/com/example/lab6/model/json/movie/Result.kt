package com.example.lab6.model.json.movie

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table")
data class Result(
    @PrimaryKey
    @SerializedName("id") var id: Int = 0,
    @SerializedName("poster_path") var posterPath: String = "",
    @SerializedName("release_date") var releaseDate: String = "",
    @SerializedName("vote_average") var voteAverage: Double = 0.0,
    @SerializedName("vote_count") var voteCount: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("original_title") var originalTitle: String = "",
    @SerializedName("overview") var overview: String = "",

    @SerializedName("runtime") var runtime: Int ?= null,
    @SerializedName("tagline") var tagline: String ?= null,

    var liked: Int? = 0,

    @Ignore var genres: List<Genre> ?= null,
    @Ignore var productionCountries: List<ProductionCountryX> ?= null
)