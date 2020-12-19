package com.example.lab6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.lab6.R
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.movie.Result
import com.example.lab6.ui.adapters.CastAdapter
import com.example.lab6.ui.adapters.MoviesAdapter
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.MovieListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.cast_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CastFragment : Fragment(), CastAdapter.RecyclerViewItemClick {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private var movieId: Int? = null

    private val movieDetailViewModel by viewModel<MovieDetailViewModel>()

    private val castAdapter: CastAdapter by lazy {
        CastAdapter(itemClickListener = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cast_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")

        bindViews(view)
        adapter()
        configureBackButton(view)
        swipeRefresh(movieId!!)
        getCast(movieId!!)
    }

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutCast)
        recyclerView = view.findViewById(R.id.rv_castAll)
    }

    private fun adapter() {
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = castAdapter
    }

    private fun swipeRefresh(id: Int) {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            castAdapter?.clearAll()
            movieDetailViewModel.getCredits(id)
        }
    }

    private fun configureBackButton(view: View) {
        val back: ImageView = view.findViewById(R.id.back)
        back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun getCast(id: Int) {
        movieDetailViewModel.getCredits(id)
        movieDetailViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieDetailViewModel.State.ShowLoading -> {
                    swipeRefreshLayoutCast.isRefreshing = true
                }
                is MovieDetailViewModel.State.HideLoading -> {
                    swipeRefreshLayoutCast.isRefreshing = false
                }
                is MovieDetailViewModel.State.Actors -> {
                    castAdapter.addItems(result.actors!!.cast)
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Cast) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val actorFragment = ActorFragment()
        actorFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frame, actorFragment).addToBackStack(null)
            .commit()
    }

}