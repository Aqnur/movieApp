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
import com.example.lab6.model.json.PaginationCounter
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.example.lab6.view.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.SharedViewModel
import com.google.firebase.analytics.FirebaseAnalytics

class MoviesFragment : Fragment(), MoviesAdapter.RecyclerViewItemClick {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movieListViewModel: MovieListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var sharedViewModel: SharedViewModel

    private var curPage = PaginationCounter.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCnt = 0

    private var moviesAdapter: MoviesAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.selected.observe(requireActivity(), Observer { item ->
            moviesAdapter?.updateItem(item)
        })
    }

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

        setViewModels()
        bindViews(view)
        swipeRefresh()
        adapter()
        getMovies(curPage)
    }

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun setViewModels() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository: MovieRepository = MovieRepositoryImpl(RetrofitService, movieDao)
        movieListViewModel = MovieListViewModel(movieRepository)

        sharedViewModel =
            activity?.run { ViewModelProvider(this).get(SharedViewModel::class.java) }
                ?: throw Exception("Invalid Activity")
    }

    private fun swipeRefresh() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            moviesAdapter?.clearAll()
            itemCnt = 0
            curPage = PaginationCounter.PAGE_START
            isLastPage = false
            movieListViewModel.getMovies(curPage)
        }
    }

    private fun adapter() {
        layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager
        moviesAdapter = MoviesAdapter(this, requireActivity())
        recyclerView.adapter = moviesAdapter

        recyclerView.addOnScrollListener(object : PaginationCounter(layoutManager) {
            override fun loadMoreItems() {
                isLoading = false
                curPage++
                getMovies(curPage)
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading
        })
    }

    private fun getMovies(page: Int) {
        movieListViewModel.getMovies(page)
        movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    moviesAdapter?.removeFooterLoading()
                    moviesAdapter?.addItems(result.list)
                    moviesAdapter?.addFooterLoading()
                    isLoading = false
                }
            }
        })
    }

    override fun addToFavourites(boolean: Boolean, position: Int, item: Result) {
        movieListViewModel.likeMovie(boolean, item, item.id)
    }

    override fun sharedView(item: Result) {
        sharedViewModel.select(item)
    }

}