package com.example.lab6.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Rating
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.example.lab6.R
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.cast.Crew
import com.example.lab6.data.model.movie.Result
import com.example.lab6.ui.adapters.CastAdapter
import com.example.lab6.ui.adapters.MediaListAdapter
import com.example.lab6.ui.adapters.ShortCastAdapter
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.SharedViewModel
import com.ms.square.android.expandabletextview.ExpandableTextView
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
    private lateinit var overview: ExpandableTextView
    private lateinit var rating: TextView
    private lateinit var votes: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var like: ImageView
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var backgroundPoster: ImageView
    private var movie: Result? = null
    private var movieId: Int? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val movieDetailsViewModel by viewModel<MovieDetailViewModel>()
    private var connectivityManager: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    private lateinit var skeletonScreen: ViewSkeletonScreen

    private lateinit var recyclerView: RecyclerView

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

        configureBackButton(view)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")

        connectivityManager =
            context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            info = connectivityManager!!.activeNetworkInfo
            if (info != null) {
                bindViews(view)
                adapter()
                getMovieCoroutine(id = movieId!!)
                getCast(movieId!!)
            } else {
                offlineStatus.visibility = View.VISIBLE
                main_lay.visibility = View.INVISIBLE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val ratingData = data?.getIntExtra("rating", 0)
            myRating.text = "Моя оценка - " + ratingData
        }
    }

    private fun getMovieCoroutine(id: Int) {
        movieDetailsViewModel.getMovie(id)
        movieDetailsViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieDetailViewModel.State.HideLoading -> {
                    skeletonScreen.hide()
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
                is MovieDetailViewModel.State.Rating -> {
                    if (result.rating!!.rated is Boolean) {
                        myRating.text = "Моя оценка - нет рейтинга"
                    } else {
                        myRating.text = "Моя оценка - " + result.rating.rated.toString()
                            .substring(7, result.rating.rated.toString().length - 3)
                    }
                }
            }
        })
    }

    private fun setCrew(crewList: List<Crew>) {
        var director: String = ""
        var producer: String = ""
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

    @SuppressLint("SetTextI18n")
    private fun setData(movie: Result) {
        rusTitle.text = movie.title

        overview.text = movie.overview
        rating.text = movie.voteAverage.toString()
        votes.text = movie.voteCount.toString()
        ratingBar.rating = movie.voteAverage.toFloat()
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .into(posterImage)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.backdrop_path}")
            .into(backgroundPoster)
        if (movie.releaseDate.isNotEmpty()) {
            titleOriginal.text = movie.originalTitle + "(" + movie.releaseDate.substring(
                0,
                movie.releaseDate.length - 6
            ) + ")"
        } else {
            titleOriginal.text = movie.originalTitle
        }
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

        like.setOnClickListener {
            if (!movie.liked) {
                like.setImageResource(R.drawable.ic_lliked)
            } else {
                like.setImageResource(R.drawable.ic_like)
            }
            likeMovie(movie)
            sharedViewModel.select(movie)
        }

        movieDetailsViewModel.isFavourite(movie.id)
        movieDetailsViewModel.isRated(movie.id)

        actors(movie.id)
        rateMovie(movie.id, movie.title)
        openTrailer(movie.id)
    }

    private fun actors(id: Int) {
        tv_showAllCast.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", id)
            val castFragment = CastFragment()
            castFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.frame, castFragment)
                .addToBackStack(null).commit()
        }
    }

    private fun rateMovie(id: Int, title: String) {
        rl_rateMovie.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putString("movieName", title)
            val ratingFragment = RatingFragment()
            ratingFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.frame, ratingFragment)
                .addToBackStack(null).commit()
        }
    }

    private fun fragmentTransactions(fragment: Fragment) {
        parentFragmentManager.beginTransaction().add(R.id.frame, fragment).addToBackStack(null)
            .commit()
    }

    private fun openTrailer(id: Int) {
        relLay1.setOnClickListener {
            var dialog = TrailerDialogFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            dialog.arguments = bundle
            dialog.show(parentFragmentManager, "TrailerDialogView")
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
        overview = view.findViewById(R.id.expandable_text_view)
        rating = view.findViewById(R.id.rating)
        votes = view.findViewById(R.id.voteCount)
        ratingBar = view.findViewById(R.id.starRating)
        like = view.findViewById(R.id.like)
        nestedScrollView = view.findViewById(R.id.nestedScrollView2)
        recyclerView = view.findViewById(R.id.rv_cast)
        backgroundPoster = view.findViewById(R.id.iv_backgroundPoster)
        skeletonScreen = Skeleton.bind(main_lay)
            .load(R.layout.movie_details_skelet_view)
            .shimmer(true)
            .color(R.color.colorOfSkeleton)
            .duration(1000)
            .show()
    }

    private fun adapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
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

    override fun itemClick(position: Int, item: Cast) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val actorFragment = ActorFragment()
        actorFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frame, actorFragment).addToBackStack(null)
            .commit()
    }

}