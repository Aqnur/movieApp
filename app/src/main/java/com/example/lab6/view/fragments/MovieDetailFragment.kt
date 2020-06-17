package com.example.lab6.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.model.api.RetrofitService
import com.example.lab6.model.database.MovieDao
import com.example.lab6.model.database.MovieDatabase
import com.example.lab6.model.json.movie.Result
import com.example.lab6.model.repository.MovieRepository
import com.example.lab6.model.repository.MovieRepositoryImpl
import com.example.lab6.view.AppContainer
import com.example.lab6.view.MoviesApplication
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.SharedViewModel

class MovieDetailFragment : Fragment() {

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
    private lateinit var movieDetailsViewModel: MovieDetailViewModel
    private var movie: Result? = null
    private var movieId: Int? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        setViewModel()
        getMovieCoroutine(id = movieId!!)
    }

    private fun setViewModel() {
        movieDetailsViewModel = MovieDetailViewModel(AppContainer.getMovieRepository())
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

    private fun likeMovie(movie: Result) {
        movieDetailsViewModel.addToFavourite(movie)
    }

    private fun setData(movie: Result) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .into(posterImage)

        if(movie.releaseDate.length != 10) {
            rusTitle.text = movie.originalTitle
        } else {
            rusTitle.text = movie.originalTitle + "(" + movie.releaseDate.substring(0, movie.releaseDate.length - 6) + ")"
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
        if (movie.runtime != null) {
            runtime.text = movie.runtime.toString() + " мин"
        }
        if (movie.tagline != null) {
            tagline.text = "«" + movie.tagline + "»"
        }

        movieDetailsViewModel.isFavourite(movie.id)

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
    }

    private fun configureBackButton(view: View) {
        val back: ImageView = view.findViewById(R.id.back)
        back.setOnClickListener {
            requireActivity().finish()
        }
    }

}