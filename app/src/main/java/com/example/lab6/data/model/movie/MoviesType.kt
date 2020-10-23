package com.example.lab6.data.model.movie

enum class MoviesType {
    POPULAR,
    FAVOURITES;

    companion object {
        fun typeToString(type: MoviesType): String {
            return when (type) {
                POPULAR -> "Popular Movies"
                FAVOURITES -> "Favourite"
            }
        }
    }
}