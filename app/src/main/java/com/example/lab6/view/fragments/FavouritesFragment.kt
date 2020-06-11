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
import com.example.lab6.view_model.FavoriteListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

class FavouritesFragment : Fragment() {

    private val TAG = "FavouriteFragment"

    lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    lateinit var recyclerViewFav: RecyclerView
    private lateinit var favoriteListViewModel: FavoriteListViewModel
    private var favoriteAdapter: MoviesAdapter?= null

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
        val viewModelProviderFactory = ViewModelProviderFactory(context = requireActivity())
        favoriteListViewModel = ViewModelProvider(this, viewModelProviderFactory).get(FavoriteListViewModel::class.java)

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
                    recyclerViewFav.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = MoviesAdapter(
                            result.list!!,
                            requireActivity()
                        )
                    }
                }
            }
        })
    }

}