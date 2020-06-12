package com.example.lab6.model.repository

import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.json.movie.PopularMovies
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import retrofit2.Response

interface MovieRepository {
    fun insertAllDB(list: List<Result>)
    fun insertDB(movie: Result)
    fun getMoviesDB():List<Result>
    fun updateMovieTagline(tagline: String, id: Int)
    fun updateMovieRuntime(runtime: Int, id: Int)
    fun getMovieById(id: Int): Result
    fun getLiked(id: Int?): Int
    fun getAllLiked(): List<Result>

    suspend fun getMovies(apiKey: String, language: String): PopularMovies?
    suspend fun getMovie(movieId: Int, apiKey: String, language: String): Result?
    suspend fun hasLike(movieId: Int, apiKey: String, sessionId: String): JsonObject?
    suspend fun markFavourite(accountId: Int, apiKey: String, sessionId: String, body: JsonObject): JsonObject?
    suspend fun getFavouriteMovies(accountId: Int, apiKey: String, sessionId: String, language: String): PopularMovies?
}

class MovieRepositoryImpl(
  private val movieApi: RetrofitService,
  private val movieDao: MovieDao
): MovieRepository {

    override fun insertAllDB(list: List<Result>) {
        return movieDao.insertAll(list)
    }

    override fun insertDB(movie: Result) {
        return movieDao.insert(movie)
    }

    override fun getMoviesDB(): List<Result> {
        return movieDao.getMovies()
    }

    override fun updateMovieTagline(tagline: String, id: Int) {
        return movieDao.updateMovieTagline(tagline, id)
    }

    override fun updateMovieRuntime(runtime: Int, id: Int) {
        return movieDao.updateMovieRuntime(runtime, id)
    }

    override fun getMovieById(id: Int): Result {
        return movieDao.getMovieById(id)
    }

    override fun getLiked(id: Int?): Int {
        return movieDao.getLiked((id))
    }

    override fun getAllLiked(): List<Result> {
        return movieDao.getAllLiked()
    }


    override suspend fun getMovies(apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getMovieListCoroutine(apiKey, language)
            .body()

    override suspend fun getMovie(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getMovieByIdCoroutine(movieId, apiKey, language)
            .body()

    override suspend fun hasLike(movieId: Int, apiKey: String, sessionId: String) =
        movieApi.getMovieApi(MovieApi::class.java).hasLikeCoroutine(movieId, apiKey, sessionId)
            .body()

    override suspend fun markFavourite(accountId: Int, apiKey: String, sessionId: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).markFavoriteMovieCoroutine(accountId, apiKey, sessionId, body)
            .body()

    override suspend fun getFavouriteMovies(accountId: Int, apiKey: String, sessionId: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getFavoriteMoviesCoroutine(accountId, apiKey, sessionId, language)
            .body()

}
