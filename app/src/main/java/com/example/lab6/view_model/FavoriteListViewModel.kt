package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.movie.GenresList
import com.example.lab6.model.repository.MovieRepositoryImpl
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

    fun getFavorites(){
        launch {
            liveData.value = State.ShowLoading

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getFavouriteMovies(
                            accountId,
                            BuildConfig.API_KEY,
                            sessionId,
                            "rus"
                        )
                        val result = response!!.results
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
            liveData.value = State.Result(list)
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