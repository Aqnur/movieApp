package com.example.lab6.domain

import com.example.lab6.data.model.movie.Result
import com.example.lab6.data.repository.MovieRepository

class LocalMoviesUse(val movieRepository: MovieRepository) {

    fun insertLocalMovies(movies: List<Result>) = movieRepository.insertAllLocalDS(movies)

    fun updateLocalMovieIsLiked(isLiked: Boolean, id: Int) =
        movieRepository.setLikeLocalDS(isLiked, id)

    fun getLocalMovie(liked: Boolean?): List<Result> = movieRepository.getMovieOfflineLocalDS(liked)

}