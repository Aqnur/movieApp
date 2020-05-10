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
import com.example.lab6.model.json.movie.GenresList
import com.example.lab6.model.json.movie.Result
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {

    private val job = Job()

    private val movieDao: MovieDao

    val liveData = MutableLiveData<State>()

    init {
        GenresList.getGenres()
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
                            for (movie in result) {
                                setMovieGenres(movie)
                            }
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
        data class Result(val list: List<com.example.lab6.model.json.movie.Result>) : State()
    }

}