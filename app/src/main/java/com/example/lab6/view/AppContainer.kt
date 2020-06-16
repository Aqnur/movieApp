package com.example.lab6.view

import android.content.Context
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.AccountRepositoryImpl
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.example.lab6.view.fragments.MoviesFragment
import com.example.lab6.view_model.ViewModelProviderFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()

    val movieRepository: MovieRepository = MovieRepositoryImpl(RetrofitService, movieDao)
    val accountRepository: AccountRepository = AccountRepositoryImpl(RetrofitService)

    val moviesViewModelFactory = MoviesViewModelFactory(movieRepository, accountRepository)
}
