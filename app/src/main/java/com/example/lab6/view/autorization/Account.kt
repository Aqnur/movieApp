package com.example.lab6.view.autorization

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.lab6.R
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.view.BaseActivity
import com.example.lab6.view_model.ProfileViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

var prof: String = "Профиль"

class Account : BaseActivity(2) {
    private val TAG = "AccountActivity"
    private var textViewName: TextView? = null

    private lateinit var logout: Button
    private lateinit var profileListViewModel: ProfileViewModel
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
        bindView()
        initViews()

        val viewModelProviderFactory = ViewModelProviderFactory(context = this@Account)
        profileListViewModel = ViewModelProvider(this, viewModelProviderFactory).get(ProfileViewModel::class.java)

        preferences = this@Account.getSharedPreferences("Username", 0) as SharedPreferences
        editor = preferences.edit()

        logout.setOnClickListener {
            editor.clear().commit()
            val intent = Intent(this@Account, LoginActivity::class.java)
            profileListViewModel.deleteProfileInform()
            startActivity(intent)
        }
    }

    private fun bindView() {
        logout = findViewById(R.id.logout)
        textViewName = findViewById<View>(R.id.profileText) as TextView
    }

    private fun initViews() {
        val authorizedName = Singleton.getUserName()
        textViewName?.text = authorizedName
    }

}
