package com.example.lab6.model.json.movie

import com.example.lab6.BuildConfig
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object GenresList : CoroutineScope {
    var genres: MutableMap<Int, String>? = HashMap()
    private var job = Job()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    fun getGenres() {
        launch {
            try {
                val response = RetrofitService.getMovieApi(MovieApi::class.java).getGenres(BuildConfig.API_KEY, "ru")
                if (response.isSuccessful) {
                    val receivedGenres = response.body()
                    if (receivedGenres != null) {
                        val genresBunch = receivedGenres.genres
                        for (genre in genresBunch) {
                            genres?.set(genre.id, genre.name)
                        }
                    }
                }
            } catch (e: Exception) {

            }

        }
    }
}