package com.example.lab6.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.lab6.data.api.RetrofitService
import com.example.lab6.data.database.MarkerDao
import com.example.lab6.data.database.MovieDao
import com.example.lab6.data.database.MovieDatabase
import com.example.lab6.data.repository.*
import com.example.lab6.view_model.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { getRetrofitService() }
}

val daoModule = module {
    single { getMovieDao(context = get()) }
    single { getMarkerDao(context = get()) }
    single { getSharedPreference(context = get()) }
}

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(movieApi = get(), movieDao = get()) }
    single<AccountRepository> { AccountRepositoryImpl(movieApi = get()) }
    single<MapRepository> { MapRepositoryImpl(markerDao = get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(accountRepository = get()) }
    viewModel { MarkersViewModel(mapRepository = get()) }
    viewModel { MovieDetailViewModel(movieRepository = get()) }
    viewModel { MovieListViewModel(movieRepository = get()) }
    viewModel { ProfileViewModel(accountRepository = get()) }
}

val appModule =  repositoryModule + viewModelModule + networkModule + daoModule

private fun getSharedPreference(context: Context) : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
private fun getRetrofitService() : RetrofitService = RetrofitService
private fun getMovieDao(context: Context) : MovieDao = MovieDatabase.getDatabase(context).movieDao()
private fun getMarkerDao(context: Context) : MarkerDao = MovieDatabase.getDatabase(context).markerDao()