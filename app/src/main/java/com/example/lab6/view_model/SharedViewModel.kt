package com.example.lab6.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.data.model.movie.Result

class SharedViewModel : BaseViewModel() {
    val selected = MutableLiveData<Result>()

    fun select(movie: Result) {
        selected.value = movie
    }
}