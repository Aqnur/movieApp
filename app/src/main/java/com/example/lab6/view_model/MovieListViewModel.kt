package com.example.lab6.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.data.model.account.Singleton
import com.example.lab6.data.model.favorites.FavResponse
import com.example.lab6.data.model.movie.MoviesType
import com.example.lab6.data.model.movie.Result
import com.example.lab6.data.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
    private var movieRepository: MovieRepository
) : BaseViewModel() {

    val liveData = MutableLiveData<State>()

    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()

    fun getMovies(type: MoviesType, page: Int = 1) {
        if (page == 1) liveData.value = State.ShowLoading
        when (type) {
            MoviesType.POPULAR -> getPopularMovies(page)
            MoviesType.TOPRATED -> getTopRatedMovies(page)
            MoviesType.UPCOMING -> getUpcomingMovies(page)
            MoviesType.NOW_PLAYING -> getNowPlayingMovies(page)
            MoviesType.FAVOURITES -> getFavorites()
            MoviesType.RATED -> getRating(page)
        }
    }

    fun getPopularMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getMoviesRemoteDS(
                        BuildConfig.API_KEY,
                        Locale.getDefault().language,
                        page
                    )
                    val result = response!!.results
                    Log.d("movies", result.toString())
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
                        for (movie in result) {
                            isFavourite(movie)
                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list, MoviesType.POPULAR)
        }
    }

    fun getTopRatedMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getTopRatedRemoteDS(
                        BuildConfig.API_KEY,
                        Locale.getDefault().language,
                        page
                    )
                    val result = response!!.results
                    Log.d("movies", result.toString())
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
//                        for (movie in result) {
//                            isFavourite(movie)
//                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list, MoviesType.TOPRATED)
        }
    }

    fun getUpcomingMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getUpcomingRemoteDS(
                        BuildConfig.API_KEY,
                        Locale.getDefault().language,
                        page
                    )
                    val result = response!!.results
                    Log.d("movies", result.toString())
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
//                        for (movie in result) {
//                            isFavourite(movie)
//                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list, MoviesType.UPCOMING)
        }
    }

    fun getNowPlayingMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getNowPlayingRemoteDS(
                        BuildConfig.API_KEY,
                        Locale.getDefault().language,
                        page
                    )
                    val result = response!!.results
                    Log.d("movies", result.toString())
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
//                        for (movie in result) {
//                            isFavourite(movie)
//                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list, MoviesType.NOW_PLAYING)
        }
    }

    fun getRating(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val rating = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getRatedRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        Locale.getDefault().language,
                        "created_at.asc"
                    )
                    val result = response!!.results
                    if (!result.isNullOrEmpty()) {
                        movieRepository.insertAllLocalDS(result)
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getMoviesLocalDS() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(rating, MoviesType.RATED)
        }
    }

    fun getFavorites() {
        launch {
            liveData.value = State.ShowLoading

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getFavouriteMoviesRemoteDS(
                        accountId,
                        BuildConfig.API_KEY,
                        sessionId,
                        Locale.getDefault().language
                    )
                    val result = response!!.results
                    Log.d("fav_movies", result.toString())
                    if (!result.isNullOrEmpty()) {
                        for (m in result) {
                            m.liked = true
                            movieRepository.setLikeLocalDS(true, m.id)
                        }
                    }
                    result
                } catch (e: Exception) {
                    movieRepository.getAllLikedLocalDS(true)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list, MoviesType.FAVOURITES)
        }
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
        launch {
            try {
                movieRepository.markFavouriteRemoteDS(
                    accountId,
                    BuildConfig.API_KEY,
                    sessionId, body
                )
            } catch (e: Exception) {
            }
        }
    }

    private fun isFavourite(movie: Result) {
        launch {
            try {
                val result =
                    movieRepository.hasLikeRemoteDS(movie.id, BuildConfig.API_KEY, sessionId)
                val gson = Gson()
                val like = gson.fromJson(
                    result,
                    FavResponse::class.java
                ).favorite
                if (like) {
                    movieRepository.setLikeLocalDS(true, movie.id)
                    movie.liked = true
                } else {
                    movieRepository.setLikeLocalDS(false, movie.id)
                    movie.liked = false
                }
            } catch (e: Exception) {
            }
        }
    }

    fun search(query: String) {
        liveData.value = State.ShowLoading
        launch {
            try {
                val response = movieRepository.searchMovieRemoteDS(
                    BuildConfig.API_KEY,
                    Locale.getDefault().language,
                    query
                )
                val result = response!!.results
                if (!result.isNullOrEmpty()) {
                    for (movie in result) {
                        isFavourite(movie)
                    }
                }
                Log.d("search", result.toString())
                liveData.value = State.Result(result)
                liveData.value = State.HideLoading
            } catch (e: Exception) {
            }
        }

    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(
            val list: List<com.example.lab6.data.model.movie.Result>?,
            val type: MoviesType? = null
        ) : State()
    }
}