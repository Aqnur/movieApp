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
import com.example.lab6.view.fragments.MoviesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var topTitle: TextView
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var activeFragment: Fragment = MoviesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        topTitle = findViewById(R.id.topTitle)

        val bottomNavigation = findViewById<BottomNavigationViewEx>(R.id.bottomNavigationView)
        bottomNavigation.onNavigationItemSelectedListener = navListener

        bottomNavigation.setIconSize(33f, 33f)
        bottomNavigation.setTextVisibility(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.enableAnimation(false)
        for (i in 0 until bottomNavigation.menu.size()) {
            bottomNavigation.setIconTintList(i, null)
        }

        fragmentManager.beginTransaction().replace(R.id.frame, MoviesFragment(), TAG).commit()
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_movies -> {
                    activeFragment = MoviesFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    topTitle.text = "Popular Movies"
                    topTitle.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_item_fav -> {
                    activeFragment = FavouritesFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    topTitle.text = "Favourite Movies"
                    topTitle.visibility = View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_item_acc -> {
                    activeFragment = AccountFragment()
                    fragmentManager.beginTransaction().replace(R.id.frame, activeFragment).commit()
                    topTitle.visibility = View.GONE
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }

}