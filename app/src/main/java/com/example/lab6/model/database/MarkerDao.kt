package com.example.lab6.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lab6.model.json.account.Marker

@Dao
interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(markers: List<Marker>)

    @Query("SELECT * FROM markers")
    fun getMarkers(): List<Marker>

    @Query("DELETE FROM markers")
    fun deleteAll()
}