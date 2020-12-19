package com.example.lab6.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.lab6.R
import com.example.lab6.view_model.MovieDetailViewModel
import kotlinx.android.synthetic.main.trailer_dialog_fragment.*
import kotlinx.android.synthetic.main.trailer_dialog_fragment.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrailerDialogFragment : DialogFragment() {

    private val movieDetailViewModel by viewModel<MovieDetailViewModel>()

    private var link: String = ""
    private var path: String = "https://www.youtube.com/embed/"
    private var movieId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.trailer_dialog_fragment, container, false)

        rootView.textView3.setOnClickListener {
            dismiss()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")

        getVideo(movieId!!)
    }

    private fun getVideo(id: Int) {
        movieDetailViewModel.getVideo(id)
        movieDetailViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieDetailViewModel.State.Videos -> {
                    val key = result.videoResponse!!.results
                    link = path + key[0].key
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(link)
                    Log.d("linkKey", link)
                }
            }
        })
    }

}