package com.example.lab6.view.activites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.lab6.*
import com.example.lab6.view.fragments.AccountFragment
import com.example.lab6.view.fragments.FavouritesFragment
import com.example.lab6.view.fragments.MovieDetailFragment
import com.example.lab6.view.fragments.MoviesFragment
import com.google.android.gms.measurement.module.Analytics
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.bottom_nav.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var topTitle: TextView
    private lateinit var bottomNavigation: BottomNavigationViewEx
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = MoviesFragment()
    private var moviesFragment: Fragment = MoviesFragment()
    private var favouritesFragment: Fragment = FavouritesFragment()
    private var accountFragment: Fragment = AccountFragment()
    private var movieDetailsFragment: Fragment = MovieDetailFragment()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        FirebaseMessaging.getInstance().subscribeToTopic("movies")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Not subscribed"
                }
                Log.d("TAGGG", msg)
            }

        bindViews()
        bottomNavigation.onNavigationItemSelectedListener = navListener
        bottomNavAnimations()
        hidingFragments()
    }

    private fun bindViews() {
        topTitle = findViewById(R.id.topTitle)
        bottomNavigation = findViewById(R.id.bottomNavigationView)
    }

    private fun bottomNavAnimations() {
        bottomNavigation.setIconSize(33f, 33f)
        bottomNavigation.setTextVisibility(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.enableAnimation(false)
        for (i in 0 until bottomNavigation.menu.size()) {
            bottomNavigation.setIconTintList(i, null)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        topTitle.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun hidingFragments() {
        fragmentManager.beginTransaction().add(R.id.frame, activeFragment).hide(moviesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, moviesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, favouritesFragment).hide(favouritesFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, accountFragment).hide(accountFragment).commit()
        fragmentManager.beginTransaction().add(R.id.frame, movieDetailsFragment).hide(movieDetailsFragment)
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_movies -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(moviesFragment)
                        .commit()
                    activeFragment = moviesFragment
                    topTitle.text = "Popular Movies"
                    topTitle.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_item_fav -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(favouritesFragment)
                        .commit()
                    activeFragment = favouritesFragment
                    topTitle.text = "Favourite Movies"
                    topTitle.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_item_acc -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(accountFragment)
                        .commit()
                    activeFragment = accountFragment
                    topTitle.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }
}