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
import com.example.lab6.view.adapters.FavouritesAdapter
import com.example.lab6.view.adapters.MoviesAdapter
import com.example.lab6.view_model.FavoriteListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_movies.*

class FavouritesFragment : Fragment(), FavouritesAdapter.RecyclerViewItemClick {

    private val TAG = "FavouriteFragment"

    lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    lateinit var recyclerViewFav: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var favoriteListViewModel: FavoriteListViewModel
    private var favoriteAdapter: FavouritesAdapter?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        swipeRefresh()
        getFavMovieCoroutine()
    }

    fun bindViews(view: View) {
        swipeRefreshLayoutFav = view.findViewById(R.id.swipeRefreshLayoutFav)
        recyclerViewFav = view.findViewById(R.id.recyclerViewFav)
    }

    fun swipeRefresh() {
        recyclerViewFav.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayoutFav.setOnRefreshListener {
            favoriteAdapter?.clearAll()
            favoriteListViewModel.getFavorites()
        }
    }

    fun getFavMovieCoroutine() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository: MovieRepository = MovieRepositoryImpl(RetrofitService, movieDao)
        favoriteListViewModel = FavoriteListViewModel(movieRepository)

        favoriteListViewModel.getFavorites()
        favoriteListViewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is FavoriteListViewModel.State.ShowLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = true
                }
                is FavoriteListViewModel.State.HideLoading -> {
                    swipeRefreshLayoutFav.isRefreshing = false
                }
                is FavoriteListViewModel.State.Result -> {
                    layoutManager = LinearLayoutManager(requireActivity())
                    recyclerViewFav.layoutManager = layoutManager
                    favoriteAdapter = FavouritesAdapter(this, result.list, requireActivity())
                    recyclerViewFav.adapter = favoriteAdapter
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

    override fun removeFromFavourites(boolean: Boolean, position: Int, item: Result) {
        favoriteListViewModel.likeMovie(boolean, item, item.id)
        favoriteAdapter?.clearAll()
        favoriteListViewModel.getFavorites()
    }

}