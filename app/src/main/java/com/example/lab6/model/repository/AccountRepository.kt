package com.example.lab6.model.repository

import com.example.lab6.model.api.ApiResponse
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.json.account.RequestToken
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response

interface AccountRepository {
    fun getRequestTokenRemoteDS(apiKey: String): Single<ApiResponse<RequestToken>>
    fun validationRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>>
    fun createSessionRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>>
    fun getAccountRemoteDS(apiKey:String, sessionId: String): Single<ApiResponse<JsonObject>>
    fun deleteSessionRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>>
}

class AccountRepositoryImpl(
    private val movieApi: RetrofitService
): AccountRepository {

    override fun getRequestTokenRemoteDS(apiKey: String): Single<ApiResponse<RequestToken>> {
        return movieApi.getMovieApi(MovieApi::class.java).getRequestToken(apiKey)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<RequestToken>("Response Error")
                }
            }
    }

    override fun validationRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).validation(apiKey, body)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }

    override fun createSessionRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).createSession(apiKey, body)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }

    override fun getAccountRemoteDS(apiKey: String, sessionId: String): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).getAccount(apiKey, sessionId)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }

    override fun deleteSessionRemoteDS(apiKey: String, body: JsonObject): Single<ApiResponse<JsonObject>> {
        return movieApi.getMovieApi(MovieApi::class.java).deleteSession(apiKey, body)
            .map { response ->
                if (response.isSuccessful) {
                    val list = response.body()
                    ApiResponse.Success(list!!)
                } else {
                    ApiResponse.Error<JsonObject>("Response Error")
                }
            }
    }

}