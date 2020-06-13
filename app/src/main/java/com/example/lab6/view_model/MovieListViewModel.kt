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

    init { }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies() {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO){
                try {
                    val response = movieRepository.getMovies(BuildConfig.API_KEY, "ru")
                        val result = response!!.results
                        if(!result.isNullOrEmpty()){
                            movieRepository.insertAllDB(result)
                        }
                        result
                } catch (e: Exception){
                    movieRepository.getMoviesDB() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }


    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()

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
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>) : State()
    }

}