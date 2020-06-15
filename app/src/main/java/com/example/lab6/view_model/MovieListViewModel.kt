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
                    val response = movieRepository.getMoviesRemoteDS(BuildConfig.API_KEY, "ru", page)
                    val favResponse = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        "rus"
                    )
                    val result = response
                    val favResult = favResponse
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
                    }
                    if (!result.isNullOrEmpty()) {
                        for (m in result) {
                            for (n in favResult!!) {
                                if(m.id == n.id) {
                                    m.liked = 1
                                }
                            }
                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list!!)
        }
    }

    fun getFavorites(){
        launch {
            liveData.value = State.ShowLoading

            val likesOffline = movieRepository.getIdOfflineLocalDS(11)

            for (i in likesOffline!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", true)
                }
                try {
                    val response = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        "rus"
                    )
                    val likeMoviesOffline = movieRepository.getMovieOfflineLocalDS(11)
                    for (movie in likeMoviesOffline!!) {
                        movie.liked = 1
                        movieRepository.insertLocalDS(movie)
                    }
                } catch (e: Exception) {
                }
            }

            val unLikesOffline = movieRepository.getIdOfflineLocalDS(10)

            for (i in unLikesOffline!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", false)
                }
                try {
                    val response = movieRepository.markFavouriteRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        body
                    )
                    val unlikeMoviesOffline = movieRepository.getMovieOfflineLocalDS(10)
                    for (movie in unlikeMoviesOffline!!) {
                        movie.liked = 0
                        movieRepository.insertLocalDS(movie)
                    }
                } catch (e: Exception) {
                }
            }

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        "rus"
                    )
                    if (!response.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(response)
                        for (m in response) {
                            m.liked = 1
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getAllLikedLocalDS()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list!!)
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
                    sessionId, body
                )
            } catch (e: Exception) { }
            if (favourite) {
                movie?.liked = 11
                if (movie != null) {
                    movieRepository.insertLocalDS(movie)
                }
            } else {
                movie?.liked = 10
                if (movie != null) {
                    movieRepository.insertLocalDS(movie)
                }
            }
            liveData.value = State.HideLoading
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>) : State()
    }

}