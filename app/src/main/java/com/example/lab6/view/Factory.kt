package com.example.lab6.view

import androidx.lifecycle.ViewModel
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.view_model.LoginViewModel
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.ProfileViewModel

interface Factory {
    fun createMovies(): MovieListViewModel
    fun createMovie(): MovieDetailViewModel
    fun createLogin(): LoginViewModel
    fun createProfile(): ProfileViewModel
}

class MoviesViewModelFactory(
    private val movieRepository: MovieRepository,
    private val accountRepository: AccountRepository
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
}