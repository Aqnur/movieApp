package com.example.lab6.data.network

import com.example.lab6.data.model.RatingResponse
import com.example.lab6.data.model.account.RequestToken
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.cast.CreditResponse
import com.example.lab6.data.model.favorites.FavResponse
import com.example.lab6.data.model.movie.Movies
import com.example.lab6.data.model.movie.RatedMovies
import com.example.lab6.data.model.movie.RatedMoviesResponse
import com.example.lab6.data.model.movie.Result
import com.example.lab6.data.model.video.VideoResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*


interface MovieApi {
    @GET("/3/movie/popular")
    suspend fun getMovieList(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("/3/movie/top_rated")
    suspend fun getTopMovies(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("/3/movie/upcoming")
    suspend fun getUpcoming(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("/3/movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("page") page: Int
    ): Response<Movies>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieById(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
        @Query("language") lang: String
    ): Response<Result>

    @GET("/3/movie/{movie_id}/credits")
    suspend fun getCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
        @Query("language") lang: String
    ): Response<CreditResponse>

    @POST("/3/movie/{movie_id}/rating")
    suspend fun rateMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Body rating: JsonObject
    ): Response<JsonObject>

    @POST("/3/account/{account_id}/favorite")
    suspend fun markFavoriteMovie(
        @Path("account_id") userId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Body favoriteRequest: JsonObject
    ): Response<JsonObject>

    @GET("/3/account/{account_id}/rated/movies")
    suspend fun getRated(
        @Path("account_id") userId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Query("language") lang: String,
        @Query("sort_by") sort: String
    ): Response<RatedMoviesResponse>

    @GET("/3/movie/{movie_id}/account_states")
    suspend fun hasLike(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<JsonObject>

    @GET("/3/movie/{movie_id}/account_states")
    suspend fun accountState(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<FavResponse>

    @GET("/3/account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") userId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Query("language") lang: String
    ): Response<Movies>

    //auth
    //new token
    @GET("/3/authentication/token/new")
    suspend fun getRequestToken(@Query("api_key") key: String): Response<RequestToken>

    //validation with token
    @POST("/3/authentication/token/validate_with_login")
    suspend fun validation(
        @Query("api_key") key: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    //create new session
    @POST("/3/authentication/session/new")
    suspend fun createSession(
        @Query("api_key") key: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    //account
    @GET("/3/account")
    suspend fun getAccount(
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String
    ): Response<JsonObject>

    //delete
    @HTTP(method = "DELETE", path = "/3/authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    @HTTP(method = "DELETE", path = "/3/movie/{movie_id}/rating", hasBody = true)
    suspend fun deleteRating(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String
    ): Response<JsonObject>

    //search
    @GET("/3/search/movie")
    suspend fun searchMovie(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("query") query: String
    ): Response<Movies>

    @GET("/3/person/{person_id}")
    suspend fun getActor(
        @Path("person_id") person_id: Int,
        @Query("api_key") key: String,
        @Query("language") lang: String
    ): Response<Cast>

    @GET("/3/movie/{movie_id}/videos")
    suspend fun getVideo(
        @Path("movie_id") movieId: Int,
        @Query("api_key") key: String,
        @Query("language") lang: String
    ): Response<VideoResponse>

    @GET("/3/movie/{movie_id}/recommendations")
    suspend fun getRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") lang: String
    ) : Response<Movies>
}
