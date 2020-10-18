package com.example.lab6.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lab6.BuildConfig
import com.example.lab6.data.model.account.Account
import com.example.lab6.data.model.account.Singleton
import com.example.lab6.data.repository.AccountRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private val accountRepository: AccountRepository
) : BaseViewModel() {

    val liveData = MutableLiveData<Account>()
    private var sessionId = Singleton.getSession()

//    private val accountRepository: AccountRepository = AccountRepositoryImpl(RetrofitService)

    fun deleteProfileInform() {
        launch {
            val body: JsonObject = JsonObject().apply {
                addProperty("session_id", sessionId)
            }
            accountRepository.deleteSessionRemoteDS(BuildConfig.API_KEY, body)
        }
    }

    fun getAccountDetail() {
        launch {
            try {
                val response = accountRepository.getAccountRemoteDS(BuildConfig.API_KEY, sessionId)
                if (response.isSuccessful) {
                    val account = Gson().fromJson(
                        response.body(),
                        Account::class.java
                    )
                    liveData.value = account
                }
            } catch (e: Exception) {

            }
        }
    }
}