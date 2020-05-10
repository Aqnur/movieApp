package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Account
import com.example.lab6.model.json.account.Singleton
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {

    val liveData = MutableLiveData<Account>()
    private val job = Job()
    private var sessionId = Singleton.getSession()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun deleteProfileInform() {
        launch {
            val body: JsonObject = JsonObject().apply {
                addProperty("session_id", sessionId)
            }
            RetrofitService.getMovieApi(MovieApi::class.java).deleteSession(BuildConfig.API_KEY, body)
        }
    }

    fun getAccountDetail() {
        launch {
            val response = RetrofitService.getMovieApi(MovieApi::class.java)
                .getAccount(BuildConfig.API_KEY, sessionId)
            if(response.isSuccessful) {
                val account = Gson().fromJson(
                    response.body(),
                    Account::class.java
                )
                liveData.value = account
            }
        }
    }
}