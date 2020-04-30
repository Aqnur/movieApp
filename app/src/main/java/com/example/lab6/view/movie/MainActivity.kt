package com.example.lab6.view.movie

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lab6.*
import com.example.lab6.view.BaseActivity
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.ViewModelProviderFactory
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(0){

    private val TAG = "MainActivity"

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView

    private lateinit var movieListViewModel: MovieListViewModel

    private var moviesAdapter: MoviesAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate")

        FirebaseMessaging.getInstance().subscribeToTopic("movies")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Not subscribed"
                }
                Log.d("TAGGG", msg)
            }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerView)

        setupBottomNavigation()
        swipeRefresh()
        getMovies()
    }

    fun swipeRefresh(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout.setOnRefreshListener {
            moviesAdapter?.clearAll()
            movieListViewModel.getMovies()
        }
    }

    fun getMovies(){
        val viewModelProviderFactory = ViewModelProviderFactory(context = this@MainActivity)
        movieListViewModel = ViewModelProvider(this@MainActivity, viewModelProviderFactory).get(MovieListViewModel::class.java)


        movieListViewModel.getMovies()
        movieListViewModel.liveData.observe(this@MainActivity, Observer { result ->
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
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter = MoviesAdapter(result.list, this@MainActivity)
                    }
                }
            }
        })
    }
}