package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.R
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.movie.GenresList
import com.example.lab6.model.json.movie.Result
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class FavoriteListViewModel(private val context: Context) : ViewModel(), CoroutineScope {

    private val job = Job()

    val liveData = MutableLiveData<State>()
    private var sessionId = Singleton.getSession()
    private var accountId = Singleton.getAccountId()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var movieDao: MovieDao? = null

    init {
        GenresList.getGenres()
        movieDao = MovieDatabase.getDatabase(context = context).movieDao()

    }

    fun getFavorites(){
        launch {
            liveData.value = State.ShowLoading

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getMovieApi(MovieApi::class.java)
                        .getFavoriteMoviesCoroutine(
                            accountId,
                            BuildConfig.API_KEY,
                            sessionId,
                            "rus"
                        )
                    if (response.isSuccessful) {
                        val result = response.body()!!.results
                        if (!result.isNullOrEmpty()) {
                            for (m in result) {
                                m.liked = 1
                            }
                        }
                        if (!result.isNullOrEmpty()) {
                            movieDao?.insertAll(result)
                            for (movie in result) {
                                setMovieGenres(movie)
                            }
                        }
                        result
                    } else {
                        movieDao?.getAllLiked()
                    }
                } catch (e: Exception) {
                    movieDao?.getAllLiked()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    private fun setMovieGenres(movie: Result) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += context.getString(R.string.genre_name, genreName)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>?) : State()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}