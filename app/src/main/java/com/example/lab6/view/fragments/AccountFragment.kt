package com.example.lab6.view.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.json.account.Account
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.model.repository.AccountRepository
import com.example.lab6.model.repository.AccountRepositoryImpl
import com.example.lab6.view.activites.GoogleMapsActivity
import com.example.lab6.view.activites.LoginActivity
import com.example.lab6.view_model.ProfileViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

class AccountFragment : Fragment() {

    private val TAG = "AccountFragment"
    private var textViewName: TextView? = null

    private lateinit var logout: Button
    private lateinit var userId: TextView
    private lateinit var userAvatar: ImageView

    private lateinit var profileListViewModel: ProfileViewModel
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        initViews()

        val accountRepository: AccountRepository = AccountRepositoryImpl(RetrofitService)
        profileListViewModel = ProfileViewModel(accountRepository)

        accountDetails()

        preferences = requireActivity().getSharedPreferences("Username", 0) as SharedPreferences
        editor = preferences.edit()

        logout.setOnClickListener {
            editor.clear().apply()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            profileListViewModel.deleteProfileInform()
            startActivity(intent)
        }
    }

    private fun accountDetails() {
        profileListViewModel.getAccountDetail()
        profileListViewModel.liveData.observe(this, Observer {
            setData(it)
        })
    }

    private fun setData(account: Account) {
        Glide.with(requireActivity())
            .load("https://secure.gravatar.com/avatar/${account.avatar.gravatar.hash}")
            .into(userAvatar)
    }

    private fun bindViews(view: View) {
        logout = view.findViewById(R.id.logout)
        textViewName = view.findViewById<View>(R.id.profileText) as TextView
        userId = view.findViewById(R.id.profileIdText)
        userAvatar = view.findViewById(R.id.userImage)

        val map = view.findViewById<Button>(R.id.map)
        map.setOnClickListener {
            val intent = Intent(requireActivity(), GoogleMapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        val authorizedName = Singleton.getUserName()
        textViewName?.text = authorizedName
        userId.text = "id:" + Singleton.getAccountId().toString()
    }

}