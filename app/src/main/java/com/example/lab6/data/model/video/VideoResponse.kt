package com.example.lab6.data.model.video

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("id") val id : Int,
    @SerializedName("results") val results : List<Videos>
)