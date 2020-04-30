package com.example.lab6.view.movie

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.model.json.movie.Result
import com.example.lab6.view_model.MovieDetailViewModel
import com.example.lab6.view_model.ViewModelProviderFactory

class MovieDetailActivity : AppCompatActivity(){

    lateinit var rusTitle: TextView
    lateinit var posterImage: ImageView
    lateinit var titleOriginal: TextView
    lateinit var genres: TextView
    lateinit var release: TextView
    lateinit var tagline: TextView
    lateinit var countries: TextView
    lateinit var runtime: TextView
    lateinit var overview: TextView
    lateinit var rating: TextView
    lateinit var votes: TextView
    lateinit var ratingBar: RatingBar
    lateinit var like: ImageView
    private lateinit var progressBar: ProgressBar
    private var movie: Result? = null
    private var movieId: Int? = null

    private lateinit var movieDetailsViewModel: MovieDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        bindViews()

        movieId = intent.getIntExtra("id", 1)

        configureBackButton()
        getMovieCoroutine(id= movieId!!)
    }

    private fun getMovieCoroutine(id: Int){
        val viewModelProviderFactory = ViewModelProviderFactory(context = this@MovieDetailActivity)
        movieDetailsViewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieDetailViewModel::class.java)

        movieDetailsViewModel.getMovie(id)
        movieDetailsViewModel.liveData.observe(this, Observer { result ->
            when(result){
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
                    } else {
                        like.setImageResource(R.drawable.ic_like)
                    }
                }
            }
        })
    }

    private fun hasLike(id: Int) {
        movieDetailsViewModel.haslike(id)
    }

    private fun likeMovie(favourite: Boolean) {
        movieDetailsViewModel.likeMovie(favourite, movie, movieId)
    }

    private fun setData(movie: Result) {
        Glide.with(this@MovieDetailActivity)
            .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
            .into(posterImage)
        var str: String = ""

        for (i in 0..3){
            str += movie.releaseDate[i]
        }

        titleOriginal.text = movie.originalTitle + "(" + str + ")"
        rusTitle.text = movie.title
        overview.text = movie.overview
        rating.text = movie.voteAverage.toString()
        votes.text = movie.voteCount.toString()
        ratingBar.rating = movie.voteAverage.toFloat()
        genres.text = getListOfString(movie.genres?.map { it.name }.toString().length, movie.genres?.map { it.name }.toString())
        if(movie.productionCountries != null) {
            countries.text = getListOfString(movie.productionCountries?.map { it.iso_3166_1}.toString().length, movie.productionCountries?.map { it.iso_3166_1 }.toString())
        }

        if (movie.runtime != null) {
            runtime.text = movie.runtime.toString() + "мин"
        }
        if (movie.tagline != null) {
            tagline.text = "«" + movie.tagline + "»"
        }

        hasLike(movie.id)

        like.setOnClickListener {
            val drawable: Drawable = like.drawable
            if( drawable.constantState?.equals(getDrawable(R.drawable.ic_like)?.constantState) == true ){
                like.setImageResource(R.drawable.ic_lliked)
                likeMovie(true)
            }else{
                like.setImageResource(R.drawable.ic_like)
                likeMovie(false)
            }
        }
    }

    fun bindViews(){
        posterImage = findViewById(R.id.moviePhoto)
        titleOriginal = findViewById(R.id.originalTitle)
        genres = findViewById(R.id.genres)
        tagline = findViewById(R.id.tagline)
        countries = findViewById(R.id.countries)
        runtime = findViewById(R.id.runtime)
        rusTitle = findViewById(R.id.rusTitle)
        overview = findViewById(R.id.overview)
        rating = findViewById(R.id.rating)
        votes = findViewById(R.id.voteCount)
        ratingBar = findViewById(R.id.starRating)
        like = findViewById(R.id.like)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun getListOfString(len: Int, s: String): String {
        var str: String = ""
        for (i in 1..len-2) {
            str += s[i]
        }
        return str
    }

    private fun configureBackButton(){
        val back: ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
    }

}
