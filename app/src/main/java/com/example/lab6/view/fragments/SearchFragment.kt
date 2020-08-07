package com.example.lab6.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.lab6.R
import com.example.lab6.model.json.movie.Result
import com.example.lab6.view.adapters.MoviesAdapter
import com.example.lab6.view.adapters.SearchAdapter
import com.example.lab6.view_model.MovieListViewModel
import com.example.lab6.view_model.SharedViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_nav.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.w3c.dom.Text

class SearchFragment : Fragment(), SearchAdapter.RecyclerViewItemClick{

    private lateinit var search: EditText
    private lateinit var searchButton: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val movieListViewModel by viewModel<MovieListViewModel>()
    private var searchAdapter: SearchAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.selected.observe(requireActivity(), Observer { item ->
            searchAdapter?.updateItem(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        search()
    }

    private fun search() {
        searchButton.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE
            movieListViewModel.search(search.text.toString())
            movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is MovieListViewModel.State.ShowLoading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                    }
                    is MovieListViewModel.State.HideLoading -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                    }
                    is MovieListViewModel.State.Result -> {
                        searchAdapter?.addItems(result.list!!)
                    }
                }
            })
        }
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager
        searchAdapter = SearchAdapter(this, requireActivity())
        recyclerView.adapter = searchAdapter
    }

    private fun bindViews(view: View) {
        search = view.findViewById(R.id.search)
        searchButton = view.findViewById(R.id.search_button)
        recyclerView = view.findViewById(R.id.searchRecycler)
        progressBar = view.findViewById(R.id.progressBar)
    }

    override fun itemClick(position: Int, item: Result) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val movieDetailFragment = MovieDetailFragment()
        movieDetailFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frame, movieDetailFragment).addToBackStack(null).commit()
        requireActivity().topTitle.visibility = View.GONE
        requireActivity().bottomNavigationView.visibility = View.GONE
    }

    override fun addToFavourite(position: Int, item: Result) {
        movieListViewModel.addToFavourite(item)
        sharedViewModel.select(item)
    }

}
