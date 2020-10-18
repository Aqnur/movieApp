package com.example.lab6.data.model.movie
import java.util.Collections.emptyList
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenresConverter {
    val gson = Gson()

    @TypeConverter
    fun fromString(data: String?): List<Genre?>? {
        if (data == null) {
            return emptyList()
        }
        val listType =
            object : TypeToken<List<Genre?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun ListToString(someObjects: List<Genre?>?): String? {
        return gson.toJson(someObjects)
    }
}
