package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.api.RetrofitService
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {

    private val job = Job()

    private val movieDao: MovieDao

    val liveData = MutableLiveData<State>()

    init {
        movieDao = MovieDatabase.getDatabase(context = context).movieDao()
    }

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
                    val response = RetrofitService.getMovieApi(
                        MovieApi::class.java).getMovieListCoroutine(BuildConfig.API_KEY, "ru")
                    if(response.isSuccessful) {
                        val result = response.body()!!.results
                        if(!result.isNullOrEmpty()){
                            movieDao.insertAll(result)
                        }
                        result
                    }else{
                        movieDao.getMovies() ?: emptyList()
                    }
                } catch (e: Exception){
                    movieDao.getMovies() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>) : State()
    }

}