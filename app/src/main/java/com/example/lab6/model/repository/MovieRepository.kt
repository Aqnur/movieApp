package com.example.lab6.model.repository

import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.json.movie.Movies
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject

interface MovieRepository {
    fun insertAllLocalDS(list: List<Result>)
    fun insertLocalDS(movie: Result)
    fun getMoviesLocalDS():List<Result>
    fun updateMovieTaglineLocalDS(tagline: String, id: Int)
    fun updateMovieRuntimeLocalDS(runtime: Int, id: Int)
    fun getMovieByIdLocalDS(id: Int): Result
    fun getLikedLocalDS(id: Int?): Int
    fun getAllLikedLocalDS(liked: Boolean): List<Result>
    fun setLikeLocalDS(liked: Boolean, id: Int)
    fun getIdOfflineLocalDS(liked: Boolean?): List<Int>
    fun getMovieOfflineLocalDS(liked: Boolean?): List<Result>

    suspend fun getMoviesRemoteDS(apiKey: String, language: String, page: Int): Movies?
    suspend fun getMovieRemoteDS(movieId: Int, apiKey: String, language: String): Result?
    suspend fun hasLikeRemoteDS(movieId: Int, apiKey: String, sessionId: String): JsonObject?
    suspend fun markFavouriteRemoteDS(accountId: Int, apiKey: String, sessionId: String, body: JsonObject): JsonObject?
    suspend fun getFavouriteMoviesRemoteDS(accountId: Int, apiKey: String, sessionId: String, language: String): Movies?
    suspend fun searchMovieRemoteDS(key: String, lang: String, query: String): Movies?
}

class MovieRepositoryImpl(
    private val movieApi: RetrofitService,
    private val movieDao: MovieDao
): MovieRepository {

    override fun insertAllLocalDS(list: List<Result>) {
        return movieDao.insertAll(list)
    }

    override fun insertLocalDS(movie: Result) {
        return movieDao.insert(movie)
    }

    override fun getMoviesLocalDS(): List<Result> {
        return movieDao.getMovies()
    }

    override fun updateMovieTaglineLocalDS(tagline: String, id: Int) {
        return movieDao.updateMovieTagline(tagline, id)
    }

    override fun updateMovieRuntimeLocalDS(runtime: Int, id: Int) {
        return movieDao.updateMovieRuntime(runtime, id)
    }

    override fun getMovieByIdLocalDS(id: Int): Result {
        return movieDao.getMovieById(id)
    }

    override fun getLikedLocalDS(id: Int?): Int {
        return movieDao.getLiked(id)
    }

    override fun getAllLikedLocalDS(liked: Boolean): List<Result> {
        return movieDao.getFavouriteMovies(liked)
    }

    override fun setLikeLocalDS(liked: Boolean, id: Int) {
        return movieDao.setLike(liked, id)
    }

    override fun getIdOfflineLocalDS(liked: Boolean?): List<Int> {
        return movieDao.getIdOffline(liked)
    }

    override fun getMovieOfflineLocalDS(liked: Boolean?): List<Result> {
        return movieDao.getMovieOffline(liked)
    }

    override suspend fun getMoviesRemoteDS(apiKey: String, language: String, page: Int) =
        movieApi.getMovieApi(MovieApi::class.java).getMovieList(apiKey, language, page)
            .body()

    override suspend fun getMovieRemoteDS(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getMovieById(movieId, apiKey, language)
            .body()

    override suspend fun hasLikeRemoteDS(movieId: Int, apiKey: String, sessionId: String) =
        movieApi.getMovieApi(MovieApi::class.java).hasLike(movieId, apiKey, sessionId)
            .body()

    override suspend fun markFavouriteRemoteDS(accountId: Int, apiKey: String, sessionId: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).markFavoriteMovie(accountId, apiKey, sessionId, body)
            .body()

    override suspend fun getFavouriteMoviesRemoteDS(accountId: Int, apiKey: String, sessionId: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getFavoriteMovies(accountId, apiKey, sessionId, language)
            .body()

    override suspend fun searchMovieRemoteDS(key: String, lang: String, query: String) =
        movieApi.getMovieApi(MovieApi::class.java).searchMovie(key, lang, query)
            .body()
}