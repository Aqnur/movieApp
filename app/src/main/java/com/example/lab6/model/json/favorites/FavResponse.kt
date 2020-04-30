package com.example.lab6.model.json.favorites

import com.google.gson.annotations.SerializedName

data class FavResponse (
@SerializedName("id")
val id: Int,
@SerializedName("favorite")
val favorite: Boolean,
@SerializedName("rated")
val rated: Any,
@SerializedName("watchlist")
val watchlist: Boolean
)