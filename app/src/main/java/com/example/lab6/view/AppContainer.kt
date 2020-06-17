package com.example.lab6.view

import android.accounts.Account
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.activityViewModels
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MarkerDao
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.repository.*
import com.example.lab6.view.fragments.MoviesFragment
import com.example.lab6.view_model.SharedViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer private constructor(context: Context) {

    companion object {
        private lateinit var INSTANCE: AppContainer
        private var initialized = false

        fun init(context: Context) {
            INSTANCE = AppContainer(context)
            initialized = true
        }

//        fun getPreferences() : SharedPreferences = INSTANCE.preferences
        fun getMovieRepository() : MovieRepository = INSTANCE.movieRepository
        fun getAccountRepository() : AccountRepository = INSTANCE.accountRepository
        fun getMapRepository() : MapRepository = INSTANCE.mapRepository
    }

//    private lateinit var preferences: SharedPreferences

    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()
    private val markerDao: MarkerDao = MovieDatabase.getDatabase(context).markerDao()

    val movieRepository: MovieRepository by lazy {
        MovieRepositoryImpl(RetrofitService, movieDao)
    }

    val accountRepository: AccountRepository by lazy {
        AccountRepositoryImpl(RetrofitService)
    }
    val mapRepository: MapRepository by lazy {
        MapRepositoryImpl(markerDao)
    }

}
