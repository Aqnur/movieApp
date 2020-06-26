package com.example.lab6.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.ApiResponse
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.favorites.FavResponse
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
        disposable.clear()
        job.cancel()
    }

    private var disposable = CompositeDisposable()

    fun getMovie(id: Int) {
        liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getMovieRemoteDS(id, BuildConfig.API_KEY, Locale.getDefault().language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when(result) {
                            is ApiResponse.Success<Result> -> {
                                Log.d("movie", result.result.toString())
                                result.result.runtime?.let { movieRepository.updateMovieRuntimeLocalDS(it, id) }
                                result.result.tagline?.let { movieRepository.updateMovieTaglineLocalDS(it, id) }
                                if(result.result.liked) {
                                    movieRepository.setLikeLocalDS(true, result.result.id)
                                }
                                liveData.value = State.HideLoading
                                liveData.value = State.Movie(result.result)
                            }
                            is ApiResponse.Error -> {
                                Log.d("movie", result.error)
                                movieRepository.getMovieByIdLocalDS(id)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("movie", error.toString())
                    }
                )
        )
    }

    fun isFavourite(movieId: Int) {
        disposable.add(
            movieRepository.hasLikeRemoteDS(movieId, BuildConfig.API_KEY, sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                { result ->
                    when (result) {
                        is ApiResponse.Success<JsonObject> -> {
                            Log.d("is_favourite", result.result.toString())
                            val gson = Gson()
                            val like = gson.fromJson(
                                result.result,
                                FavResponse::class.java
                            ).favorite
                            if (like) {
                                movieRepository.setLikeLocalDS(true, movieId)
                                liveData.value = State.Res(1)
                            } else {
                                movieRepository.setLikeLocalDS(false, movieId)
                                liveData.value = State.Res(0)
                            }
                        }
                        is ApiResponse.Error -> {
                            movieRepository.getLikedLocalDS(movieId) ?: 0
                            Log.d("is_favourite", result.error)
                        }
                    }
                }
        )
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
        disposable.add(
            movieRepository.markFavouriteRemoteDS(accountId, BuildConfig.API_KEY, sessionId, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                { result ->
                    when (result) {
                        is ApiResponse.Success<JsonObject> -> {
                            Log.d("mark_favourite", result.result.toString())
                        }
                        is ApiResponse.Error -> {
                            Log.d("mark_favourite", result.error)
                        }
                    }
                }
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Movie(val movie: Result?) : State()
        data class Res(val likeInt: Int?) : State()
    }
}