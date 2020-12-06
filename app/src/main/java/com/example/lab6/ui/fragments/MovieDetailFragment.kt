package com.example.lab6.ui.fragments

import android.graphics.drawable.Drawable
import android.media.Rating
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.data.model.cast.Crew
import com.example.lab6.data.model.movie.Result
import com.example.lab6.ui.adapters.CastAdapter
import com.example.lab6.ui.adapters.ShortCastAdapter
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.cast_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailFragment : Fragment(), ShortCastAdapter.RecyclerViewItemClick {

    private lateinit var rusTitle: TextView
    private lateinit var posterImage: ImageView
    private lateinit var titleOriginal: TextView
    private lateinit var genres: TextView
    private lateinit var tagline: TextView
    private lateinit var countries: TextView
    private lateinit var runtime: TextView
    private lateinit var overview: TextView
    private lateinit var rating: TextView
    private lateinit var votes: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var like: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var backgroundPoster: ImageView
    private var movie: Result? = null
    private var movieId: Int? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val movieDetailsViewModel by viewModel<MovieDetailViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private val shortCastAdapter: ShortCastAdapter by lazy {
        ShortCastAdapter(itemClickListener = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")

        configureBackButton(view)
        adapter()
        getMovieCoroutine(id = movieId!!)
        getCast(movieId!!)
    }

    private fun getMovieCoroutine(id: Int) {
        movieDetailsViewModel.getMovie(id)
        movieDetailsViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieDetailViewModel.State.ShowLoading -> {
                    progressBar.visibility = ProgressBar.VISIBLE
                }
                is MovieDetailViewModel.State.HideLoading -> {
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
                is MovieDetailViewModel.State.Movie -> {
                    movie = result.movie
                    setData(movie!!)
                }
                is MovieDetailViewModel.State.Res -> {
                    if (result.likeInt == 1 || result.likeInt == 11) {
                        like.setImageResource(R.drawable.ic_lliked)
                        movie?.liked = true
                    } else {
                        like.setImageResource(R.drawable.ic_like)
                        movie?.liked = false
                    }
                }
            }
        })
    }

    private fun setCrew(crewList: List<Crew>) {
        var director : String = ""
        var producer : String = ""
        for (crew in crewList) {
            if (crew.job == "Director") director += crew.name + ", "
            if (crew.job == "Producer") producer += crew.name + ", "
        }
        tv_directors.text = director
        tv_producers.text = producer
    }

    private fun getCast(id: Int) {
        movieDetailsViewModel.getCredits(id)
        movieDetailsViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieDetailViewModel.State.Actors -> {
                    shortCastAdapter.addItems(result.actors!!.cast)
                    setCrew(result.actors.crew)
                }
            }
        })
    }

    private fun likeMovie(movie: Result) {
        movieDetailsViewModel.addToFavourite(movie)
    }

    private fun setData(movie: Result) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .into(posterImage)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.backdrop_path}")
            .into(backgroundPoster)

        if(movie.releaseDate.length != 10) {
            titleOriginal.text = movie.originalTitle
        } else {
            titleOriginal.text = movie.originalTitle + "(" + movie.releaseDate.substring(0, movie.releaseDate.length - 6) + ")"
        }
        rusTitle.text = movie.title
        overview.text = movie.overview
        rating.text = movie.voteAverage.toString()
        votes.text = movie.voteCount.toString()
        ratingBar.rating = movie.voteAverage.toFloat()
        if (movie.genres != null) {
            genres.text = movie.genres?.map { it.name }.toString()
                .substring(1, movie.genres?.map { it.name }.toString().length - 1)
        }
        if (movie.productionCountries != null) {
            countries.text = movie.productionCountries?.map { it.iso_3166_1 }.toString()
                .substring(
                    1,
                    movie.productionCountries?.map { it.iso_3166_1 }.toString().length - 1
                )
        }

        movieDetailsViewModel.isFavourite(movie.id)

        if (movie.runtime != null) {
            runtime.text = movie.runtime.toString() + " мин"
        }
        if (movie.tagline != null) {
            tagline.text = "«" + movie.tagline + "»"
        }

        like.setOnClickListener {
            val drawable: Drawable = like.drawable
            if (!movie.liked) {
                like.setImageResource(R.drawable.ic_lliked)
            } else {
                like.setImageResource(R.drawable.ic_like)
            }
            likeMovie(movie)
            sharedViewModel.select(movie)
        }

        actors(movie.id)

        relLay3.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", movie.id)
            val ratingFragment = RatingFragment()
            ratingFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.frame, ratingFragment).addToBackStack(null).commit()
        }
    }

    private fun actors(id: Int) {
        tv_showAllCast.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", id)
            val castFragment = CastFragment()
            castFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.frame, castFragment).addToBackStack(null).commit()
        }
    }

    private fun bindViews(view: View) {
        posterImage = view.findViewById(R.id.moviePhoto)
        titleOriginal = view.findViewById(R.id.originalTitle)
        genres = view.findViewById(R.id.genres)
        tagline = view.findViewById(R.id.tagline)
        countries = view.findViewById(R.id.countries)
        runtime = view.findViewById(R.id.runtime)
        rusTitle = view.findViewById(R.id.rusTitle)
        overview = view.findViewById(R.id.overview)
        rating = view.findViewById(R.id.rating)
        votes = view.findViewById(R.id.voteCount)
        ratingBar = view.findViewById(R.id.starRating)
        like = view.findViewById(R.id.like)
        progressBar = view.findViewById(R.id.progressBar)
        nestedScrollView = view.findViewById(R.id.nestedScrollView2)
        recyclerView = view.findViewById(R.id.rv_cast)
        backgroundPoster = view.findViewById(R.id.iv_backgroundPoster)
    }

    private fun adapter() {
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = shortCastAdapter
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
        TODO("Not yet implemented")
    }

}