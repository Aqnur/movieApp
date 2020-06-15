package com.example.lab6.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import com.example.lab6.view.adapters.FavouritesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.SharedViewModel

class FavouritesFragment : Fragment(), FavouritesAdapter.RecyclerViewItemClick {

    private lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    private lateinit var recyclerViewFav: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var movieListViewModel: MovieListViewModel
    private var favoriteAdapter: FavouritesAdapter? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.selected.observe(viewLifecycleOwner, Observer { item ->
            if (item.liked == 1 || item.liked == 11) favoriteAdapter?.addItem(item)
            else favoriteAdapter?.removeItem(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        bindViews(view)
        swipeRefresh()
        setAdapter()
        getFavMovieCoroutine()
    }

    private fun bindViews(view: View) {
        swipeRefreshLayoutFav = view.findViewById(R.id.swipeRefreshLayoutFav)
        recyclerViewFav = view.findViewById(R.id.recyclerViewFav)
    }

    private fun setViewModel() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository: MovieRepository = MovieRepositoryImpl(RetrofitService, movieDao)
        movieListViewModel = MovieListViewModel(movieRepository)
    }

    private fun swipeRefresh() {
        recyclerViewFav.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayoutFav.setOnRefreshListener {
            favoriteAdapter?.clearAll()
            movieListViewModel.getFavorites()
        }
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(requireActivity())
        recyclerViewFav.layoutManager = layoutManager
        favoriteAdapter = FavouritesAdapter(this, requireActivity())
        recyclerViewFav.adapter = favoriteAdapter
    }

    private fun getFavMovieCoroutine() {
        movieListViewModel.getFavorites()
        movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    favoriteAdapter?.addItems(result.list!!)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayoutFav.isRefreshing = true
        getFavMovieCoroutine()
        swipeRefreshLayoutFav.isRefreshing = false
    }

    override fun addToFavourites(boolean: Boolean, position: Int, item: Result) {
        movieListViewModel.likeMovie(boolean, item, item.id)
        sharedViewModel.select(item)
    }

}