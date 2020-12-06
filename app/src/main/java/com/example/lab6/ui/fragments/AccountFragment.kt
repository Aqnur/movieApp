package com.example.lab6.ui.fragments

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
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.data.model.account.Account
import com.example.lab6.data.model.account.Singleton
import com.example.lab6.ui.activites.GoogleMapsActivity
import com.example.lab6.ui.activites.LoginActivity
import com.example.lab6.view_model.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : Fragment() {

    private lateinit var logout: Button
    private lateinit var userAvatar: ImageView
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var preferences: SharedPreferences
    private var textViewName: TextView? = null
    private val profileListViewModel by viewModel<ProfileViewModel>()

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
        accountDetails()

        preferences = requireActivity().getSharedPreferences("Username", 0) as SharedPreferences

        logout.setOnClickListener {
            preferences.edit().clear().commit()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            profileListViewModel.deleteProfileInform()
            Singleton.reset()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun accountDetails() {
        profileListViewModel.getAccountDetail()
        profileListViewModel.liveData.observe(viewLifecycleOwner, Observer {
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
    }

}