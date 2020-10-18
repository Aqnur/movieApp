package com.example.lab6.data.repository

import com.example.lab6.data.database.MarkerDao
import com.example.lab6.data.model.account.Marker

interface MapRepository {
    fun insertAll(markers: List<Marker>)
    fun getMarkers(): List<Marker>
    fun deleteAll()
}

class MapRepositoryImpl(private val markerDao: MarkerDao): MapRepository {
    override fun insertAll(markers: List<Marker>) {
        return markerDao.insertAll(markers)
    }

    override fun getMarkers(): List<Marker> {
        return markerDao.getMarkers()
    }

    override fun deleteAll() {
        return markerDao.deleteAll()
    }

}