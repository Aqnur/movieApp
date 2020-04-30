package com.example.lab6.view.autorization

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lab6.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.json.account.User
import com.example.lab6.view.movie.MainActivity
import com.example.lab6.view_model.LoginViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class LoginActivity : AppCompatActivity(){

    private var nestedScrollView: NestedScrollView? = null
    private var textInputLayoutEmail: TextInputLayout? = null
    private var textViewLinkRegister: AppCompatTextView? = null
    private var textViewLinkForgotPassword: AppCompatTextView? = null
    private lateinit var textInputLayoutPassword: TextInputLayout

    private lateinit var password: EditText
    private lateinit var login: EditText
    private lateinit var appCompatButtonLogin: AppCompatButton
    private lateinit var progressBar: ProgressBar
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()

        val viewModelProviderFactory = ViewModelProviderFactory(context = this@LoginActivity)
        loginViewModel = ViewModelProvider(this, viewModelProviderFactory).get(LoginViewModel::class.java)

        stayLogged()

        loginViewModel.liveData.observe(this, Observer {
            when (it) {
                is LoginViewModel.State.ShowLoading -> {
                    progressBar.visibility = ProgressBar.VISIBLE
                }
                is LoginViewModel.State.HideLoading -> {
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
                is LoginViewModel.State.BadResult -> {
                    check()
                }
                is LoginViewModel.State.Result -> {
                    loginCoroutine(it.json)
                }
            }
        })

        textViewLinkRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        appCompatButtonLogin.setOnClickListener {
            loginViewModel.makeToken(login.text.toString(), password.text.toString())
        }
    }

    private fun check() {
        if (login.text.toString() == "" || password.text.toString() == "") {
            Toast.makeText(this@LoginActivity, "Empty username or password", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@LoginActivity, "Something is wrong", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loginCoroutine(data: String?) {
        try {
            preferences = this@LoginActivity.getSharedPreferences("Username", 0)
            preferences.edit().putString("user", data).apply()
            val gsonGen = Gson()
            val type: Type = object : TypeToken<User>() {}.type
            val user = gsonGen.fromJson<User>(data, type)
            openApp(user)
        } catch (e: Exception) {
        }
    }

    private fun stayLogged() {
        try {
            preferences = this@LoginActivity.getSharedPreferences("Username", 0)
            val gsonGen = Gson()
            val json: String? = preferences.getString("user", null)
            val type: Type = object : TypeToken<User>() {}.type
            val user = gsonGen.fromJson<User>(json, type)

            openApp(user)
        } catch (e: Exception) {
        }
    }

    private fun openApp(user: User) {
        if (user.sessionId != "") {
            var MySingleton =
                Singleton.create(
                    user.username,
                    user.sessionId,
                    user.accountId
                )
            val intent = Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            startActivity(
                intent
            )
        }
    }

    private fun initViews() {
        textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
        appCompatButtonLogin = findViewById<View>(R.id.appCompatButtonLogin) as AppCompatButton
        textViewLinkRegister = findViewById<View>(R.id.textViewLinkRegister) as AppCompatTextView
        textViewLinkForgotPassword = findViewById<View>(R.id.forgotPassword) as AppCompatTextView
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
    }
}