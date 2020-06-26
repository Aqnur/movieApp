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
    fun getMovieList(@Query("api_key") key: String,
                                      @Query("language") lang: String,
                                      @Query("page") page:Int) : Single<Response<Movies>>

    @GET("/3/movie/{movie_id}")
    fun getMovieById(@Path("movie_id") movieId: Int,
                              @Query("api_key") key: String,
                              @Query("language") lang: String) : Single<Response<Result>>

    @POST("/3/account/{account_id}/favorite")
    fun markFavoriteMovie(@Path("account_id") userId: Int,
                          @Query("api_key") key: String,
                          @Query("session_id") sessionId: String,
                          @Body favoriteRequest: JsonObject): Single<Response<JsonObject>>

    @GET("/3/movie/{movie_id}/account_states")
    fun hasLike(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?): Single<Response<JsonObject>>

    @GET("/3/account/{account_id}/favorite/movies")
     fun getFavoriteMovies(@Path("account_id") userId: Int,
                          @Query("api_key") key: String,
                          @Query("session_id") sessionId: String,
                          @Query("language") lang: String): Single<Response<Movies>>

    @GET("/3/genre/movie/list")
    suspend fun getGenres(@Query("api_key") apiKey: String, @Query("language") lang: String): Response<Genres>

    //auth
    //new token
    @GET("/3/authentication/token/new")
    fun getRequestToken(@Query("api_key") key: String): Single<Response<RequestToken>>

    //validation with token
    @POST("/3/authentication/token/validate_with_login")
    fun validation(
        @Query("api_key") key: String,
        @Body body: JsonObject) : Single<Response<JsonObject>>

    //create new session
    @POST("/3/authentication/session/new")
    fun createSession(
        @Query("api_key") key: String,
        @Body body: JsonObject) : Single<Response<JsonObject>>

    //account
    @GET("/3/account")
    fun getAccount(
        @Query("api_key") key:String,
        @Query("session_id") sessionId: String): Single<Response<JsonObject>>

    //delete
    @HTTP(method = "DELETE",path = "/3/authentication/session",hasBody = true)
    fun deleteSession(@Query("api_key") apiKey: String, @Body body: JsonObject): Single<Response<JsonObject>>

}
