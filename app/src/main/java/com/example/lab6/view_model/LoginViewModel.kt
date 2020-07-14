package com.example.lab6.view_model

import android.content.Context
import android.util.Log
import android.view.contentcapture.ContentCaptureSessionId
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.ApiResponse
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Account
import com.example.lab6.model.json.account.Session
import com.example.lab6.model.json.account.RequestToken
import com.example.lab6.model.json.account.User
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.AccountRepositoryImpl
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    var liveData = MutableLiveData<State>()

    private lateinit var requestToken: String
    private lateinit var newRequestToken: String
    private var json: String = ""
    private var disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun makeToken(name: String, password: String) {
        liveData.value = State.ShowLoading
        disposable.add(
            accountRepository.getRequestTokenRemoteDS(BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<RequestToken> -> {
                                Log.d("request_token", result.result.toString())
                                requestToken = result.result.requestToken.toString()
                                responseToken(name, password)
                            }
                            is ApiResponse.Error -> {
                                Log.d("request_token", result.error)
                                liveData.value = State.BadResult
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("request_token", error.toString())
                    }
                )
        )
    }

    private fun responseToken(name: String, password: String) {
        val body = JsonObject().apply {
            addProperty("username", name)
            addProperty("password", password)
            addProperty("request_token", requestToken)
        }
        disposable.add(
            accountRepository.validationRemoteDS(BuildConfig.API_KEY, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<JsonObject> -> {
                                Log.d("response_token", result.result.toString())
                                    val newCreatedToken = Gson().fromJson(
                                        result.result,
                                        RequestToken::class.java
                                    )
                                    newRequestToken = newCreatedToken.requestToken
                                    getSession(name, body)
                            }
                            is ApiResponse.Error -> {
                                Log.d("response_token", result.error)
                                liveData.value = State.BadResult
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("response_token", error.toString())
                    }
                )
        )
    }

    private fun getSession(name: String, body: JsonObject) {
        disposable.add(
            accountRepository.createSessionRemoteDS(BuildConfig.API_KEY, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<JsonObject> -> {
                                Log.d("session", result.result.toString())
                                val newSession = Gson().fromJson(
                                    result.result,
                                    Session::class.java
                                )
                                val sessionId = newSession.sessionId
                                getAccountId(name, sessionId)
                            }
                            is ApiResponse.Error -> {
                                Log.d("session", result.error)
                                liveData.value = State.BadResult
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("session", error.toString())
                    }
                )
        )
    }

    private fun getAccountId(name: String, sessionId: String) {
        disposable.add(
            accountRepository.getAccountRemoteDS(BuildConfig.API_KEY, sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<JsonObject> -> {
                                Log.d("Account", result.result.toString())
                                val newAccId = Gson().fromJson(
                                    result.result,
                                    Account::class.java
                                )
                                val idAcc = newAccId.id
                                val user = User(name, sessionId, idAcc)
                                json = Gson().toJson(user)
                                liveData.value = State.Result(json)
                            }
                            is ApiResponse.Error -> {
                                Log.d("Account", result.error)
                                liveData.value = State.BadResult
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("Account", error.toString())
                    }
                )
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val json: String?) : State()
        object BadResult : State()
    }

}