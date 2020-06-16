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
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieDetailViewModel(
    private val movieRepository: MovieRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()
    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()
    val liveData = MutableLiveData<State>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init {
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovie(id: Int) {
        liveData.value = State.ShowLoading
        launch {
            val movieDetail = withContext(Dispatchers.IO){
                try {
                    val response = movieRepository.getMovieRemoteDS(id, BuildConfig.API_KEY, Locale.getDefault().language)
                        if (response != null) {
                            response.runtime?.let { movieRepository.updateMovieRuntimeLocalDS(it, id) }
                            response.tagline?.let { movieRepository.updateMovieTaglineLocalDS(it, id) }
                            if(response.liked) {
                                movieRepository.setLikeLocalDS(true, response.id)
                            }
                        }
                        response
                } catch (e: Exception) {
                    movieRepository.getMovieByIdLocalDS(id)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Movie(movieDetail)
        }
    }

    fun haslike(movieId: Int) {
        launch {
            val likeInt = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.hasLikeRemoteDS(
                            movieId,
                            BuildConfig.API_KEY,
                            sessionId
                        )
                    Log.d("TAG", response.toString())
                        val gson = Gson()
                        val like = gson.fromJson(
                            response,
                            FavResponse::class.java
                        ).favorite
                        if (like) {
                            movieRepository.setLikeLocalDS(true, movieId)
                            1
                        } else {
                            movieRepository.setLikeLocalDS(false, movieId)
                            0
                        }
                } catch (e: Exception) {
                    movieRepository.getLikedLocalDS(movieId) ?: 0
                }
            }
            liveData.value = State.Res(likeInt)
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

    fun likeMovie(favourite: Boolean, movie: Result?, movieId: Int?) {
        liveData.value = State.ShowLoading
        launch {
            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movieId)
                addProperty("favorite", favourite)
            }
            try {
                movieRepository.markFavouriteRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId, body)
            } catch (e: Exception) { }
            if (favourite) {
                movie?.liked = true
                if (movie != null) {
                    movieRepository.insertLocalDS(movie)
                    movieRepository.setLikeLocalDS(true, movie.id)
                }
            } else {
                movie?.liked = false
                if (movie != null) {
                    movieRepository.setLikeLocalDS(false, movie.id)
                    movieRepository.insertLocalDS(movie)
                }
            }
            liveData.value = State.HideLoading
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Movie(val movie: Result?) : State()
        data class Res(val likeInt: Int?) : State()
    }
}