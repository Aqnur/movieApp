package com.example.lab6.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.lab6.model.json.account.Marker
import com.example.lab6.model.database.MarkerDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.json.account.generateMarkers
import com.example.lab6.model.repository.MapRepository
import com.example.lab6.model.repository.MapRepositoryImpl
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MarkersViewModel(context: Context) : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var markerDao: MarkerDao = MovieDatabase.getDatabase(context = context).markerDao()
    private val mapRepository: MapRepository = MapRepositoryImpl(markerDao)

    fun fillDatabase() {
        launch {
            withContext(Dispatchers.IO) {
                mapRepository.deleteAll()
                mapRepository.insertAll(generateMarkers())
            }
        }
    }

    fun getMarkers(): List<Marker> = runBlocking {
        return@runBlocking getMarkersFromDatabase()
    }

    private suspend fun getMarkersFromDatabase(): List<Marker> {
        return withContext(Dispatchers.IO) {
            return@withContext mapRepository.getMarkers() as MutableList
        }
    }

}