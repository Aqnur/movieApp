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
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.example.lab6.view.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import com.google.firebase.analytics.FirebaseAnalytics

class MoviesFragment : Fragment(), MoviesAdapter.RecyclerViewItemClick {

    private val TAG = "MoviesFragment"

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var movieListViewModel: MovieListViewModel
    private var moviesAdapter: MoviesAdapter? = null
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

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun swipeRefresh() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            moviesAdapter?.clearAll()
            movieListViewModel.getMovies()
        }
    }

    private fun getMovies() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository: MovieRepository = MovieRepositoryImpl(RetrofitService, movieDao)

        movieListViewModel = MovieListViewModel(movieRepository)

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
                    layoutManager = LinearLayoutManager(requireActivity())
                    recyclerView.layoutManager = layoutManager
                    moviesAdapter = MoviesAdapter(this, result.list, requireActivity())
                    recyclerView.adapter = moviesAdapter
                }
            }
        })
    }

    override fun addToFavourites(boolean: Boolean, position: Int, item: Result) {
        movieListViewModel.likeMovie(boolean, item, item.id)
    }

}