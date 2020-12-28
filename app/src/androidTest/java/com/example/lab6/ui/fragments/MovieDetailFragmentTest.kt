package com.example.lab6.ui.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.lab6.R
import com.example.lab6.data.database.MovieDao
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.movie.Result
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MovieDetailFragmentTest {

    @Test
    fun test_isActorVisible() {

        val dao : MovieDao ?= null

        val movie : Result = dao!!.getMovieById(1)
        val bundle = Bundle()
        bundle.putInt("movieId", movie.id)

        val scenario = launchFragmentInContainer<ActorFragment>(
            fragmentArgs = bundle
        )

        Espresso.onView(ViewMatchers.withId(R.id.originalTitle))
            .check(ViewAssertions.matches(ViewMatchers.withText(movie.originalTitle)))
    }
}