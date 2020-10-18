package com.example.lab6.domain

import com.example.lab6.BuildConfig
import com.example.lab6.data.repository.MovieRepository
import java.util.*

class GetMoviesUseCase(private val movieRepository: MovieRepository) {

    suspend fun getMovieList(page: Int) =
        movieRepository.getMoviesRemoteDS(
            BuildConfig.API_KEY, Locale.getDefault().language, page
        )

    suspend fun getFavouriteMovies(accountId: Int, sessionId: String) =
        movieRepository.getFavouriteMoviesRemoteDS(
            accountId,
            BuildConfig.API_KEY,
            sessionId,
            Locale.getDefault().language
        )

    suspend fun searchMovies(query: String) =
        movieRepository.searchMovieRemoteDS(
            BuildConfig.API_KEY,
            Locale.getDefault().language,
            query
        )

    suspend fun getMovieDetail(id: Int) =
        movieRepository.getMovieRemoteDS(
            id, BuildConfig.API_KEY,
            Locale.getDefault().language
        )
}