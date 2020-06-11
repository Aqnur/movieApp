package com.example.lab6.model.json.account

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class Marker(
    @PrimaryKey
    var id: Int,
    val lat: Double,
    val lng: Double,
    val title: String
)

fun generateMarkers(): List<Marker> {
    val markers: MutableList<Marker> = arrayListOf()
    val marker1 = Marker(
        1,
        43.336636,
        76.952979,
        "Arman 3D"
    )
    markers.add(marker1)
    val marker2 = Marker(
        2,
        43.262158,
        76.941380,
        "Lumiera Cinema"
    )
    markers.add(marker2)
    val marker3 = Marker(
        3,
        43.267937,
        76.934299,
        "Illusion ATRIUM"
    )
    markers.add(marker3)
    return markers
}