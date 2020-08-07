package com.example.lab6.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.json.account.Account
import com.example.lab6.model.json.account.RequestToken
import com.example.lab6.model.json.account.Session
import com.example.lab6.model.json.account.User
import com.example.lab6.model.repository.AccountRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginViewModel(private val accountRepository: AccountRepository) : ViewModel(), CoroutineScope {

    private val job = Job()

    var liveData = MutableLiveData<State>()

    private lateinit var requestToken: String
    private lateinit var newRequestToken: String
    private var json: String = ""

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun makeToken(name: String, password: String) {
        liveData.value = State.ShowLoading
        launch {
            val response = accountRepository.getRequestTokenRemoteDS(BuildConfig.API_KEY)
            if(response.isSuccessful){
                requestToken = response.body()?.requestToken.toString()
                responseToken(name, password)
            }else{
                liveData.value = State.BadResult
                liveData.value = State.HideLoading
            }
        }
    }

    private fun responseToken(name: String, password: String) {
        launch {
            val body = JsonObject().apply {
                addProperty("username", name)
                addProperty("password", password)
                addProperty("request_token", requestToken)
            }
            val responseLogin = accountRepository.validationRemoteDS(BuildConfig.API_KEY, body)

            if(responseLogin.isSuccessful) {
                val newCreatedToken = Gson().fromJson(
                    responseLogin.body(),
                    RequestToken::class.java
                )
                newRequestToken = newCreatedToken.requestToken
                getSession(name, body)
            } else {
                liveData.value = State.BadResult
                liveData.value = State.HideLoading
            }
        }
    }

    private fun getSession(name: String, body: JsonObject) {
        launch {
            val responseSession = accountRepository.createSessionRemoteDS(BuildConfig.API_KEY, body)
            if(responseSession.isSuccessful) {
                val newSession = Gson().fromJson(
                    responseSession.body(),
                    Session::class.java
                )
                val sessionId = newSession.sessionId
                getAccountId(name, sessionId)
            } else {
                liveData.value = State.BadResult
                liveData.value = State.HideLoading
            }
        }
    }

    private fun getAccountId(name: String, sessionId: String) {
        launch {
            val response = accountRepository.getAccountRemoteDS(BuildConfig.API_KEY, sessionId)
            if (response.isSuccessful) {
                val newAccId = Gson().fromJson(
                    response.body(),
                    Account::class.java
                )
                val idAcc = newAccId.id
                val user = User(name, sessionId, idAcc)
                json = Gson().toJson(user)
                liveData.value = State.Result(json)
            } else {
                liveData.value = State.BadResult
                liveData.value = State.HideLoading
            }
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val json: String?) : State()
        object BadResult : State()
    }

}