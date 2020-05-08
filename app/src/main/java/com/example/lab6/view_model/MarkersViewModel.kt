package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.lab6.model.Marker
import com.example.lab6.model.database.MarkerDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.generateMarkers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MarkersViewModel(context: Context) : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var markerDao: MarkerDao = MovieDatabase.getDatabase(context = context).markerDao()

    fun fillDatabase() {
        launch {
            withContext(Dispatchers.IO) {
                markerDao.deleteAll()
                markerDao.insertAll(generateMarkers())
            }
        }
    }

    fun getMarkers(): List<Marker> = runBlocking {
        return@runBlocking getMarkersFromDatabase()
    }

    private suspend fun getMarkersFromDatabase(): List<Marker> {
        return withContext(Dispatchers.IO) {
            return@withContext markerDao.getMarkers() as MutableList
        }
    }

}