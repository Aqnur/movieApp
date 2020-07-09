package com.example.lab6.model.repository

import com.example.lab6.model.api.ApiResponse
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.json.movie.Movies
import com.example.lab6.model.json.movie.PopularMovies
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response

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

    fun getMoviesRemoteDS(apiKey: String, language: String, page: Int): Single<ApiResponse<List<Result>>>
    fun getMovieRemoteDS(movieId: Int, apiKey: String, language: String): Single<ApiResponse<Result>>
    fun hasLikeRemoteDS(movieId: Int, apiKey: String, sessionId: String): Single<ApiResponse<JsonObject>>
    fun markFavouriteRemoteDS(accountId: Int, apiKey: String, sessionId: String, body: JsonObject): Single<ApiResponse<JsonObject>>
    fun getFavouriteMoviesRemoteDS(accountId: Int, apiKey: String, sessionId: String, language: String): Single<ApiResponse<List<Result>>>
    fun searchMovieRemoteDS(key: String, lang: String, query: String): Single<ApiResponse<List<Result>>>
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

    override fun getMoviesRemoteDS(
        apiKey: String,
        language: String,
        page: Int
    ): Single<ApiResponse<List<Result>>> {
        return movieApi.getMovieApi(MovieApi::class.java).getMovieList(apiKey, language, page)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()?.results ?: emptyList()
                    ApiResponse.Success(list)
                } else {
                    ApiResponse.Error<List<Result>>("Response Error")
                }
            }
    }

    override fun getMovieRemoteDS(movieId: Int, apiKey: String, language: String): Single<ApiResponse<Result>> {
        return movieApi.getMovieApi(MovieApi::class.java).getMovieById(movieId, apiKey, language)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<Result>("Response Error")
                }
            }
    }
    override fun hasLikeRemoteDS(movieId: Int, apiKey: String, sessionId: String): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).hasLike(movieId, apiKey, sessionId)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }

    override fun markFavouriteRemoteDS(accountId: Int, apiKey: String, sessionId: String, body: JsonObject): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).markFavoriteMovie(accountId, apiKey, sessionId, body)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }


    override fun getFavouriteMoviesRemoteDS(accountId: Int, apiKey: String, sessionId: String, language: String): Single<ApiResponse<List<Result>>> {
        return movieApi.getMovieApi(MovieApi::class.java).getFavoriteMovies(accountId, apiKey, sessionId, language)
            .map { response->
                if(response.isSuccessful) {
                    val list = response.body()?.results ?: emptyList()
                    ApiResponse.Success(list)
                } else {
                    ApiResponse.Error<List<Result>>("Response Error")
                }
            }
    }

    override fun searchMovieRemoteDS(
        key: String,
        lang: String,
        query: String
    ): Single<ApiResponse<List<Result>>> {
        return movieApi.getMovieApi(MovieApi::class.java).searchMovie(key, lang, query)
            .map { response->
                if(response.isSuccessful) {
                    val list = response.body()?.results ?: emptyList()
                    ApiResponse.Success(list)
                } else {
                    ApiResponse.Error<List<Result>>("Response Error")
                }
            }
    }
}
