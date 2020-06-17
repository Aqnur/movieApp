package com.example.lab6.view

import androidx.lifecycle.ViewModel
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.MapRepository
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.view_model.*

interface Factory {
    fun createMovies(): MovieListViewModel
    fun createMovie(): MovieDetailViewModel
    fun createLogin(): LoginViewModel
    fun createProfile(): ProfileViewModel
    fun createMap(): MarkersViewModel
}

class MoviesViewModelFactory(
    private val movieRepository: MovieRepository,
    private val accountRepository: AccountRepository,
    private val mapRepository: MapRepository
) : Factory {

    override fun createMovies(): MovieListViewModel {
        return MovieListViewModel(movieRepository)
    }

    override fun createMovie(): MovieDetailViewModel {
        return MovieDetailViewModel(movieRepository)
    }

    override fun createLogin(): LoginViewModel {
        return LoginViewModel(accountRepository)
    }

    override fun createProfile(): ProfileViewModel {
        return ProfileViewModel(accountRepository)
    }

    override fun createMap(): MarkersViewModel {
        return MarkersViewModel(mapRepository)
    }
}