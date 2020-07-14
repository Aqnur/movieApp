package com.example.lab6.model.json

import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject

data class FinalResult(
    val movie: Result,
    val isFavourite: JsonObject
)