package com.example.lab6.model.repository

import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.json.account.RequestToken
import com.google.gson.JsonObject
import retrofit2.Response

interface AccountRepository {
    suspend fun getRequestTokenRemoteDS(apiKey: String): Response<RequestToken>
    suspend fun validationRemoteDS(apiKey: String, body: JsonObject): Response<JsonObject>
    suspend fun createSessionRemoteDS(apiKey: String, body: JsonObject): Response<JsonObject>
    suspend fun getAccountRemoteDS(apiKey:String, sessionId: String): Response<JsonObject>
    suspend fun deleteSessionRemoteDS(apiKey: String, body: JsonObject): Response<JsonObject>
}

class AccountRepositoryImpl(
    private val movieApi: RetrofitService
): AccountRepository {

    override suspend fun getRequestTokenRemoteDS(apiKey: String) =
        movieApi.getMovieApi(MovieApi::class.java).getRequestToken(apiKey)

    override suspend fun validationRemoteDS(apiKey: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).validation(apiKey, body)

    override suspend fun createSessionRemoteDS(apiKey: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).createSession(apiKey, body)

    override suspend fun getAccountRemoteDS(apiKey: String, sessionId: String) =
        movieApi.getMovieApi(MovieApi::class.java).getAccount(apiKey, sessionId)

    override suspend fun deleteSessionRemoteDS(apiKey: String, body: JsonObject) =
        movieApi.getMovieApi(MovieApi::class.java).deleteSession(apiKey, body)

}