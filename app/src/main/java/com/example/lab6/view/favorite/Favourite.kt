package com.example.lab6.view.favorite

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lab6.view.BaseActivity
import com.example.lab6.R
import com.example.lab6.view.movie.MoviesAdapter
import com.example.lab6.view_model.FavoriteListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

class Favourite : BaseActivity(1){

    private val TAG = "FavouriteActivity"

    lateinit var swipeRefreshLayoutFav: SwipeRefreshLayout
    lateinit var recyclerViewFav: RecyclerView
    private lateinit var favoriteListViewModel: FavoriteListViewModel
    private var favoriteAdapter: FavoriteAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        swipeRefreshLayoutFav = findViewById(R.id.swipeRefreshLayoutFav)
        recyclerViewFav = findViewById(R.id.recyclerViewFav)

        swipeRefresh()
        getFavMovieCoroutine()
    }

    fun swipeRefresh(){
        recyclerViewFav.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayoutFav.setOnRefreshListener {
            favoriteAdapter?.clearAll()
            favoriteListViewModel.getFavorites()
        }
    }

    fun getFavMovieCoroutine(){
        val viewModelProviderFactory = ViewModelProviderFactory(context = this@Favourite)
        favoriteListViewModel = ViewModelProvider(this@Favourite, viewModelProviderFactory).get(FavoriteListViewModel::class.java)

        favoriteListViewModel.getFavorites()
        favoriteListViewModel.liveData.observe(this@Favourite, Observer { result ->
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
                        layoutManager = LinearLayoutManager(this@Favourite)
                        adapter = MoviesAdapter(result.list!!, this@Favourite)
                    }
                }
            }
        })
    }
}
