package com.example.lab6.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.model.api.ApiResponse
import com.example.lab6.model.api.MovieApi
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.json.account.Account
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.AccountRepositoryImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val accountRepository: AccountRepository
) : ViewModel(), CoroutineScope {

    val liveData = MutableLiveData<Account>()
    private val job = Job()
    private var sessionId = Singleton.getSession()
    private var disposable = CompositeDisposable()

//    private val accountRepository: AccountRepository = AccountRepositoryImpl(RetrofitService)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        job.cancel()
    }

    fun deleteProfileInform() {
        val body: JsonObject = JsonObject().apply {
            addProperty("session_id", sessionId)
        }
        disposable.add(
            accountRepository.deleteSessionRemoteDS(BuildConfig.API_KEY, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<JsonObject> -> {
                                Log.d("delete_profile", result.result.toString())
                            }
                            is ApiResponse.Error -> {
                                Log.d("delete_profile", result.error)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("delete_profile", error.toString())
                    }
                )
        )
    }

    fun getAccountDetail() {
        disposable.add(
            accountRepository.getAccountRemoteDS(BuildConfig.API_KEY, sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is ApiResponse.Success<JsonObject> -> {
                                Log.d("acc_detail", result.result.toString())
                                val account = Gson().fromJson(
                                    result.result,
                                    Account::class.java
                                )
                                liveData.value = account
                            }
                            is ApiResponse.Error -> {
                                Log.d("acc_detail", result.error)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("acc_detail", error.toString())
                    }
                )
        )
    }
}