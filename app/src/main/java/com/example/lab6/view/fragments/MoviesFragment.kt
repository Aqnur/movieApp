package com.example.lab6.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lab6.R
import com.example.lab6.view.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import com.google.firebase.analytics.FirebaseAnalytics

class MoviesFragment : Fragment() {

    private val TAG = "MoviesFragment"

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    private lateinit var movieListViewModel: MovieListViewModel
    private var moviesAdapter: MoviesAdapter?= null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        bindViews(view)
        swipeRefresh()
        getMovies()
    }

    fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    fun swipeRefresh() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            moviesAdapter?.clearAll()
            movieListViewModel.getMovies()
        }
    }

    fun getMovies() {
        val viewModelProviderFactory = ViewModelProviderFactory(context = requireActivity())
        movieListViewModel = ViewModelProvider(this, viewModelProviderFactory).get(
            MovieListViewModel::class.java)

        movieListViewModel.getMovies()
        movieListViewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = MoviesAdapter(
                            result.list,
                            requireActivity()
                        )
                    }
                }
            }
        })
    }
}