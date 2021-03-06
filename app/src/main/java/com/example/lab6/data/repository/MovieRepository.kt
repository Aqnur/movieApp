package com.example.lab6.data.repository

import com.example.lab6.data.network.MovieApi
import com.example.lab6.data.network.RetrofitService
import com.example.lab6.data.database.MovieDao
import com.example.lab6.data.model.RatingResponse
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.cast.CreditResponse
import com.example.lab6.data.model.favorites.FavResponse
import com.example.lab6.data.model.movie.Movies
import com.example.lab6.data.model.movie.RatedMoviesResponse
import com.example.lab6.data.model.movie.Result
import com.example.lab6.data.model.video.VideoResponse
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
    suspend fun getTopRatedRemoteDS(apiKey: String, language: String, page: Int): Movies?
    suspend fun getUpcomingRemoteDS(apiKey: String, language: String, page: Int): Movies?
    suspend fun getNowPlayingRemoteDS(apiKey: String, language: String, page: Int): Movies?
    suspend fun getCreditsRemoteDS(movieId: Int, apiKey: String, language: String) : CreditResponse?
    suspend fun rateMovieRemoteDS(movieId: Int, apiKey: String, sessionId: String, rating: JsonObject) : JsonObject?
    suspend fun getRatedRemoteDS(userId: Int, apiKey: String, sessionId: String, language: String, sort: String) : Movies?
    suspend fun deleteRating(movieId: Int, apiKey: String, sessionId: String) : JsonObject?
    suspend fun getActor(personId: Int, apiKey: String, language: String) : Cast?
    suspend fun getVideo(movieId: Int, apiKey: String, language: String) : VideoResponse?
    suspend fun accountState(movieId: Int, apiKey: String, sessionId: String) : FavResponse?
    suspend fun getRecommendations(movieId: Int, apiKey: String, language: String) : Movies?
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

    override suspend fun getTopRatedRemoteDS(apiKey: String, language: String, page: Int) =
        movieApi.getMovieApi(MovieApi::class.java).getTopMovies(apiKey, language, page)
            .body()

    override suspend fun getUpcomingRemoteDS(apiKey: String, language: String, page: Int) =
        movieApi.getMovieApi(MovieApi::class.java).getUpcoming(apiKey, language, page)
            .body()

    override suspend fun getNowPlayingRemoteDS(apiKey: String, language: String, page: Int) =
        movieApi.getMovieApi(MovieApi::class.java).getNowPlaying(apiKey, language, page)
            .body()

    override suspend fun getMovieRemoteDS(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getMovieById(movieId, apiKey, language)
            .body()

    override suspend fun getActor(personId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getActor(personId, apiKey, language)
            .body()

    override suspend fun getVideo(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getVideo(movieId, apiKey, language)
            .body()

    override suspend fun getCreditsRemoteDS(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getCredits(movieId, apiKey, language)
            .body()

    override suspend fun hasLikeRemoteDS(movieId: Int, apiKey: String, sessionId: String) =
        movieApi.getMovieApi(MovieApi::class.java).hasLike(movieId, apiKey, sessionId)
            .body()

    override suspend fun accountState(movieId: Int, apiKey: String, sessionId: String) =
        movieApi.getMovieApi(MovieApi::class.java).accountState(movieId, apiKey, sessionId)
            .body()

    override suspend fun getRecommendations(movieId: Int, apiKey: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getRecommendations(movieId, apiKey, language)
            .body()

    override suspend fun markFavouriteRemoteDS(accountId: Int, apiKey: String, sessionId: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).markFavoriteMovie(accountId, apiKey, sessionId, body)
            .body()

    override suspend fun rateMovieRemoteDS(movieId: Int, apiKey: String, sessionId: String, rating: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).rateMovie(movieId, apiKey, sessionId, rating)
            .body()

    override suspend fun getRatedRemoteDS(
        userId: Int,
        apiKey: String,
        sessionId: String,
        language: String,
        sort: String
    ) = movieApi.getMovieApi(MovieApi::class.java).getRated(userId, apiKey, sessionId, language, sort).body()

    override suspend fun deleteRating(
        movieId: Int,
        apiKey: String,
        sessionId: String
    ) = movieApi.getMovieApi(MovieApi::class.java).deleteRating(movieId, apiKey, sessionId).body()

    override suspend fun getFavouriteMoviesRemoteDS(accountId: Int, apiKey: String, sessionId: String, language: String) =
        movieApi.getMovieApi(MovieApi::class.java).getFavoriteMovies(accountId, apiKey, sessionId, language)
            .body()

    override suspend fun searchMovieRemoteDS(key: String, lang: String, query: String) =
        movieApi.getMovieApi(MovieApi::class.java).searchMovie(key, lang, query)
            .body()
}