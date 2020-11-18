package com.example.lab6.ui.fragments

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
import com.example.lab6.data.model.PaginationCounter
import com.example.lab6.data.model.movie.MoviesType
import com.example.lab6.data.model.movie.Result
import com.example.lab6.ui.adapters.MediaListAdapter
import com.example.lab6.ui.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.SharedViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movies.*
import kotlinx.android.synthetic.main.bottom_nav.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var popularMoviesRV: RecyclerView
    private lateinit var topRatedMoviesRV: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val movieListViewModel by viewModel<MovieListViewModel>()
    private var curPage = PaginationCounter.PAGE_START

    private val popularMoviesAdapter: MediaListAdapter by lazy {
        MediaListAdapter(itemClickListener = itemClickListener, context = requireContext())
    }

    private val topRatedMoviesAdapter: MediaListAdapter by lazy {
        MediaListAdapter(itemClickListener = itemClickListener, context = requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.selected.observe(requireActivity(), Observer { item ->
            popularMoviesAdapter?.updateItem(item)
            topRatedMoviesAdapter?.updateItem(item)
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

        bindViews(view)
        swipeRefresh()
        adapter()
        getMovies(MoviesType.POPULAR)
        getMovies(MoviesType.TOPRATED)
        observe()
    }

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        popularMoviesRV = view.findViewById(R.id.rv_popularMovies)
        topRatedMoviesRV = view.findViewById(R.id.rv_topRatedMovies)

        tv_popularMovies.setOnClickListener {
            goToMoviesList(MoviesType.POPULAR)
        }

        tv_topRatedMovies.setOnClickListener {
            goToMoviesList(MoviesType.TOPRATED)
        }
    }

    private fun goToMoviesList(type: MoviesType) {
        val bundle = Bundle()
        bundle.putSerializable("type", type)

        val movieFragment = MoviesFragment()
        movieFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frame, movieFragment).addToBackStack(null)
            .commit()
        requireActivity().topTitle.visibility = View.GONE
        requireActivity().bottomNavigationView.visibility = View.GONE
    }

    private fun swipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            topRatedMoviesAdapter.clearAll()
            popularMoviesAdapter.clearAll()
            curPage = PaginationCounter.PAGE_START
            getMovies(MoviesType.POPULAR)
            getMovies(MoviesType.TOPRATED)
        }
    }

    private fun adapter() {

        popularMoviesRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topRatedMoviesRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        popularMoviesRV.adapter = popularMoviesAdapter
        topRatedMoviesRV.adapter = topRatedMoviesAdapter

    }

    private fun getMovies(type: MoviesType) {
        movieListViewModel.getMovies(type, 1)
    }

    private fun observe() {
        movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    if (result.type == MoviesType.POPULAR) {
                        popularMoviesAdapter.addItems(result.list!!)
                    }
                    if (result.type == MoviesType.TOPRATED) {
                        topRatedMoviesAdapter.addItems(result.list!!)
                    }
                }
            }
        })
    }

    private val itemClickListener = object : MediaListAdapter.RecyclerViewItemClick {
        override fun itemClick(position: Int, item: Result) {
            val bundle = Bundle()
            bundle.putInt("id", item.id)
            val movieDetailFragment = MovieDetailFragment()
            movieDetailFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.frame, movieDetailFragment)
                .addToBackStack(null).commit()
            requireActivity().topTitle.visibility = View.GONE
            requireActivity().bottomNavigationView.visibility = View.GONE
        }
    }
}