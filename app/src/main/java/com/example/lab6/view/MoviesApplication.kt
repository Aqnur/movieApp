package com.example.lab6.view

import android.app.Application

class MoviesApplication : Application() {

    lateinit var  appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}