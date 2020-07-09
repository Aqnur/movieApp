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
        disposable.clear()
        job.cancel()
    }

    private var disposable = CompositeDisposable()

    fun getMovies(page: Int = 1) {
        if (page == 1) liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getMoviesRemoteDS(
                BuildConfig.API_KEY,
                Locale.getDefault().language,
                page
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<List<Result>> -> {
                                Log.d("movies", result.result.toString())
                                for(movie in result.result) {
                                    isFavourite(movie)
                                }
                                movieRepository.insertAllLocalDS(result.result)
                                liveData.value = State.HideLoading
                                liveData.value = State.Result(result.result)
                            }
                            is ApiResponse.Error -> {
                                Log.d("movies", result.error)
                                movieRepository.getMoviesLocalDS()
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("movies", error.toString())
                        movieRepository.getMoviesLocalDS()
                    }
                )
        )
    }

    fun getFavorites() {
        liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getFavouriteMoviesRemoteDS(
                accountId,
                BuildConfig.API_KEY,
                sessionId,
                Locale.getDefault().language
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<List<Result>> -> {
                                Log.d("fav_movies", result.result.toString())
                                for (m in result.result) {
                                    m.liked = true
                                    movieRepository.setLikeLocalDS(true, m.id)
                                }
                                liveData.value = State.HideLoading
                                liveData.value = State.Result(result.result)
                            }
                            is ApiResponse.Error -> {
                                Log.d("fav_movies", result.error)
                                movieRepository.getAllLikedLocalDS(true)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("fav_movies", error.toString())
                        movieRepository.getAllLikedLocalDS(true)
                    }
                )
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

    private fun isFavourite(movie: Result) {
        disposable.add(
            movieRepository.hasLikeRemoteDS(movie.id, BuildConfig.API_KEY, sessionId)
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
                                movieRepository.setLikeLocalDS(true, movie.id)
                                movie.liked = true
                            } else {
                                movieRepository.setLikeLocalDS(false, movie.id)
                                movie.liked = false
                            }
                        }
                        is ApiResponse.Error -> {
                            movieRepository.getLikedLocalDS(movie.id) ?: 0
                            Log.d("is_favourite", result.error)
                        }
                    }
                }
        )
    }

    fun search(query: String) {
        disposable.add(
            movieRepository.searchMovieRemoteDS(BuildConfig.API_KEY, Locale.getDefault().language, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<List<Result>> -> {
                                Log.d("search", result.result.toString())
                                liveData.value = State.HideLoading
                                liveData.value = State.Result(result.result)
                            }
                            is ApiResponse.Error -> {
                                Log.d("search", result.error)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("search", error.toString())
                    }
                )
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>?) : State()
    }

}