package com.example.lab6.model.json.favorites

import com.google.gson.annotations.SerializedName


data class FavoriteRequest (
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("favorite") val favorite: Boolean
)
