package com.example.lab6.model.database

import androidx.room.*
import com.example.lab6.model.json.movie.Result

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Result>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Result)

    @Query("SELECT * FROM movie_table")
    fun getMovies(): List<Result>

    @Query("SELECT * FROM movie_table WHERE id = :id")
    fun getMovieById(id: Int): Result

    @Query("UPDATE movie_table SET tagline = :tagline WHERE id = :id")
    fun updateMovieTagline(tagline: String, id: Int)

    @Query("UPDATE movie_table SET runtime = :runtime WHERE id = :id")
    fun updateMovieRuntime(runtime: Int, id: Int)

    //favorites

    @Query("SELECT*FROM movie_table where liked=:liked")
    fun getMovieOffline(liked: Boolean?): List<Result>

    @Query("SELECT * FROM movie_table WHERE liked = :liked")
    fun getFavouriteMovies(liked: Boolean): List<Result>

    @Query("update movie_table set liked = :likeCnt where id = :id")
    fun setLike(likeCnt: Boolean, id: Int)

    @Query("SELECT liked FROM movie_table where id=:id")
    fun getLiked(id: Int?): Int

    @Query("SELECT id FROM movie_table where liked=:liked")
    fun getIdOffline(liked: Boolean?): List<Int>

}

