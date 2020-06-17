package com.example.lab6.view

import android.app.Application

class MoviesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(applicationContext)
    }

}