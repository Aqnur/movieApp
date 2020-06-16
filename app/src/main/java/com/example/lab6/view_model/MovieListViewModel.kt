package com.example.lab6.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.favorites.FavResponse
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
    private var movieRepository: MovieRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()

    val liveData = MutableLiveData<State>()

    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getMoviesRemoteDS(BuildConfig.API_KEY, Locale.getDefault().language, page)
                    val favResponse = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        Locale.getDefault().language
                    )
                    if (!response.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(response)
                    }
                    if (!response.isNullOrEmpty()) {
                        for (m in response) {
                            for (n in favResponse!!) {
                                if(m.id == n.id) {
                                    m.liked = true
                                    movieRepository.setLikeLocalDS(true, m.id)
                                }
                            }
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun getFavorites(){
        launch {
            liveData.value = State.ShowLoading

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        Locale.getDefault().language
                    )
                    if (!response.isNullOrEmpty()) {
                        for (m in response) {
                            m.liked = true
                            movieRepository.setLikeLocalDS(true, m.id)
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getAllLikedLocalDS(true)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun addToFavourite(movie: Result) {
        movie.liked = !movie.liked
        val body = JsonObject().apply {
            addProperty("media_type", "movie")
            addProperty("media_id", movie.id)
            addProperty("favorite", movie.liked)
        }
        updateFavourite(body)
        movieRepository.insertLocalDS(movie)
    }

    private fun updateFavourite(body: JsonObject) {
        launch {
            try {
                movieRepository.markFavouriteRemoteDS(
                    accountId,
                    BuildConfig.API_KEY,
                    sessionId, body
                )
            } catch (e: Exception) { }
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>?) : State()
    }

}