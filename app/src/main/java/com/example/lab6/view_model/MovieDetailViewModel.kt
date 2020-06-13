package com.example.lab6.view_model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.favorites.FavResponse
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
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
                    val response = movieRepository.getMovie(id, BuildConfig.API_KEY, "ru")
                        val result = response
                        if (result != null) {
                            result.runtime?.let { movieRepository.updateMovieRuntime(it, id) }
                            result.tagline?.let { movieRepository.updateMovieTagline(it, id) }
                        }
                        result
                } catch (e: Exception) {
                    movieRepository.getMovieById(id)
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
                    val response = movieRepository.hasLike(
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
                        if (like)
                            1
                        else 0
                } catch (e: Exception) {
                    movieRepository.getLiked(movieId) ?: 0
                }
            }
            liveData.value = State.Res(likeInt)
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
                movieRepository.markFavourite(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId, body)
            } catch (e: Exception) { }
            if (favourite) {
                movie?.liked = 11
                if (movie != null) {
                    movieRepository.insertDB(movie)
                }
//                Toast.makeText(context, "Movie has been added to favourites", Toast.LENGTH_SHORT).show()
            } else {
                movie?.liked = 10
                if (movie != null) {
                    movieRepository.insertDB(movie)
                }
//                Toast.makeText(context,"Movie has been removed from favourites", Toast.LENGTH_SHORT).show()
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