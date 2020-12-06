package com.example.lab6.view_model

import android.nfc.Tag
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.data.model.RatingResponse
import com.example.lab6.data.model.account.Singleton
import com.example.lab6.data.model.cast.CreditResponse
import com.example.lab6.data.model.favorites.FavResponse
import com.example.lab6.data.model.movie.Result
import com.example.lab6.data.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieDetailViewModel(
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()
    val liveData = MutableLiveData<State>()

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

    fun getCredits(id: Int) {
        liveData.value = State.ShowLoading
        launch {
            val actors = withContext(Dispatchers.IO) {
                val response = movieRepository.getCreditsRemoteDS(id, BuildConfig.API_KEY, Locale.getDefault().language)
                response
            }
            liveData.value = State.HideLoading
            liveData.value = State.Actors(actors)
        }
    }

    fun getRating(movieId: Int) {
        launch {
            val rating = withContext(Dispatchers.IO) {
                val response = movieRepository.getRatedRemoteDS(accountId, BuildConfig.API_KEY, sessionId, Locale.getDefault().language, "created_at.asc")
            }
        }
    }

    fun isFavourite(movieId: Int) {
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

    fun rateMovie(movieId: Int, value: Float){
        val body = JsonObject().apply {
            addProperty("value", value)
        }
        updateRating(movieId, body)
    }

    private fun updateRating(movieId: Int, body: JsonObject) {
        launch {
            try {
                movieRepository.rateMovieRemoteDS(
                    movieId,
                    BuildConfig.API_KEY,
                    sessionId, body
                )
                Log.d("rating", movieId.toString() + " " + sessionId + " " + body.toString())
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
        data class Actors(val actors: CreditResponse?) : State()
    }
}