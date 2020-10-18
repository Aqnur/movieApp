package com.example.lab6.view_model

import androidx.lifecycle.ViewModel
import com.example.lab6.data.model.account.Marker
import com.example.lab6.data.model.account.generateMarkers
import com.example.lab6.data.repository.MapRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MarkersViewModel(private val mapRepository: MapRepository) : BaseViewModel() {

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