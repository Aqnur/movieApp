package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Singleton
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {
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
}