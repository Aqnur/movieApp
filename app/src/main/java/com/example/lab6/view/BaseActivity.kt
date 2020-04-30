package com.example.lab6.view

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.lab6.R
import com.example.lab6.view.autorization.Account
import com.example.lab6.view.movie.MainActivity
import com.example.lab6.view.favorite.Favourite
import kotlinx.android.synthetic.main.bottom_nav.*

abstract class BaseActivity(val navNumber: Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"

    fun setupBottomNavigation() {
        bottomNavigationView.setIconSize(33f, 33f)
        bottomNavigationView.setTextVisibility(false)
        bottomNavigationView.enableItemShiftingMode(false)
        bottomNavigationView.enableShiftingMode(false)
        bottomNavigationView.enableAnimation(false)
        for (i in 0 until bottomNavigationView.menu.size()) {
            bottomNavigationView.setIconTintList(i, null)
        }
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val nextActivity =
                when (it.itemId) {
                    R.id.nav_item_movies -> MainActivity::class.java
                    R.id.nav_item_fav -> Favourite::class.java
                    R.id.nav_item_acc -> Account::class.java
                    else -> {
                        Log.e(TAG, "unknown nav item clicked $it")
                        null
                    }
                }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
        bottomNavigationView.menu.getItem(navNumber).isChecked = true
    }
}