package com.example.lab6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.lab6.ui.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.SharedViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.fragment_popular_movies.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment(), MoviesAdapter.RecyclerViewItemClick {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var movieType: MoviesType = MoviesType.POPULAR
    private val movieListViewModel by viewModel<MovieListViewModel>()

    private var curPage = PaginationCounter.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCnt = 0

    private val moviesAdapter: MoviesAdapter by lazy {
        MoviesAdapter(itemClickListner = this, context = requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = this.arguments

        if (bundle != null) {
            movieType = bundle.get("type") as MoviesType
        }

        return inflater.inflate(R.layout.fragment_popular_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        tv_movieType.text = movieType.toString()

        bindViews(view)
        swipeRefresh()
        adapter()
        configureBackButton(view)
        getMovies(curPage)
    }

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun swipeRefresh() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            moviesAdapter?.clearAll()
            itemCnt = 0
            curPage = PaginationCounter.PAGE_START
            isLastPage = false
            movieListViewModel.getPopularMovies(curPage)
        }
    }

    private fun adapter() {
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = moviesAdapter

        recyclerView.addOnScrollListener(object : PaginationCounter(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                curPage++
                getMovies(curPage)
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading
        })
    }

    private fun getMovies(page: Int) {
        movieListViewModel.getMovies(movieType, page)
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
                    moviesAdapter?.addItems(result.list!!)
                    moviesAdapter?.addFooterLoading()
                    isLoading = false
                }
            }
        })
    }

    private fun configureBackButton(view: View) {
        val back: ImageView = view.findViewById(R.id.back)
        back.setOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().topTitle.visibility = View.VISIBLE
            requireActivity().bottomNavigationView.visibility = View.VISIBLE
        }
    }

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

    override fun addToFavourites(position: Int, item: Result) {
        movieListViewModel.addToFavourite(item)
        sharedViewModel.select(item)
    }

}