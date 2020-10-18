package com.example.lab6.data.model

import com.example.lab6.data.model.movie.Result
import com.google.gson.JsonObject

data class FinalResult(
    val movie: Result,
    val isFavourite: JsonObject
)