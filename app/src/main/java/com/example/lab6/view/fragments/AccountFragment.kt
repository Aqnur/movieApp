package com.example.lab6.view.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab6.R
import com.example.lab6.model.json.account.Singleton
import com.example.lab6.view.activites.LoginActivity
import com.example.lab6.view_model.ProfileViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

class AccountFragment : Fragment() {

    private val TAG = "AccountFragment"
    private var textViewName: TextView? = null

    private lateinit var logout: Button
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
        val viewModelProviderFactory = ViewModelProviderFactory(context = requireActivity())
        profileListViewModel = ViewModelProvider(this, viewModelProviderFactory).get(ProfileViewModel::class.java)

        preferences = requireActivity().getSharedPreferences("Username", 0) as SharedPreferences
        editor = preferences.edit()

        logout.setOnClickListener {
            editor.clear().commit()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            profileListViewModel.deleteProfileInform()
            startActivity(intent)
        }
    }

    private fun bindViews(view: View) {
        logout = view.findViewById(R.id.logout)
        textViewName = view.findViewById<View>(R.id.profileText) as TextView
    }

    private fun initViews() {
        val authorizedName = Singleton.getUserName()
        textViewName?.text = authorizedName
    }

}