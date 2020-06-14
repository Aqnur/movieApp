package com.example.lab6.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.model.json.movie.Result

class SharedViewModel : ViewModel() {
    val selected = MutableLiveData<Result>()

    fun select(movie: Result) {
        selected.value = movie
    }
}