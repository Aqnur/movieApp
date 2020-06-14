package com.example.lab6.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class FavoriteListViewModel(private val movieRepository: MovieRepository) : ViewModel(), CoroutineScope {

    private val job = Job()

    val liveData = MutableLiveData<State>()
    private var sessionId = Singleton.getSession()
    private var accountId = Singleton.getAccountId()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init { }

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

    fun getFavorites(){
        launch {
            liveData.value = State.ShowLoading

            val likesOffline = movieRepository.getIdOffline(11)

            for (i in likesOffline!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", true)
                }
                try {
                    val response = movieRepository.getFavouriteMovies(
                            accountId,
                            BuildConfig.API_KEY,
                            sessionId,
                            "rus"
                        )
                    val likeMoviesOffline = movieRepository.getMovieOffline(11)
                    for (movie in likeMoviesOffline!!) {
                        movie.liked = 1
                        movieRepository.insertDB(movie)
                    }
                } catch (e: Exception) {
                }
            }

            val unLikesOffline = movieRepository.getIdOffline(10)

            for (i in unLikesOffline!!) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", false)
                }
                try {
                    val response = movieRepository.markFavourite(
                            accountId,
                            BuildConfig.API_KEY,
                            sessionId,
                            body
                        )
                    val unlikeMoviesOffline = movieRepository.getMovieOffline(10)
                    for (movie in unlikeMoviesOffline!!) {
                        movie.liked = 0
                        movieRepository.insertDB(movie)
                    }
                } catch (e: Exception) {
                }
            }

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getFavouriteMovies(
                            accountId,
                            BuildConfig.API_KEY,
                            sessionId,
                            "rus"
                        )
                        val result = response
                        if (!result.isNullOrEmpty()) {
                            for (m in result) {
                                m.liked = 1
                            }
                        }
                        if (!result.isNullOrEmpty()) {
                            movieRepository.insertAllDB(result)
                        }
                        result
                } catch (e: Exception) {
                    movieRepository.getAllLiked()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list!!)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>) : State()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}