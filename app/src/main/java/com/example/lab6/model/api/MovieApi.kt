package com.example.lab6.model.api

import com.example.lab6.model.json.account.RequestToken
import com.example.lab6.model.json.movie.Genres
import com.example.lab6.model.json.movie.Movies
import com.example.lab6.model.json.movie.PopularMovies
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*


interface MovieApi {
    @GET("/3/movie/popular")
    suspend fun getMovieList(
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

    @POST("/3/account/{account_id}/favorite")
    suspend fun markFavoriteMovie(
        @Path("account_id") userId: Int,
        @Query("api_key") key: String,
        @Query("session_id") sessionId: String,
        @Body favoriteRequest: JsonObject
    ): Response<JsonObject>

    @GET("/3/movie/{movie_id}/account_states")
    suspend fun hasLike(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<JsonObject>

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

    //search
    @GET("/3/search/movie")
    suspend fun searchMovie(
        @Query("api_key") key: String,
        @Query("language") lang: String,
        @Query("query") query: String
    ): Response<Movies>

}
