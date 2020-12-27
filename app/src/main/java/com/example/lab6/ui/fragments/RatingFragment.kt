package com.example.lab6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.lab6.R
import com.example.lab6.view_model.MovieDetailViewModel
import kotlinx.android.synthetic.main.rating_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RatingFragment : Fragment() {

    private lateinit var ratingBar: RatingBar
    private var movieId: Int? = null
    private var movieTitle: String? = ""

    private val movieDetailsViewModel by viewModel<MovieDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rating_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratingBar = view.findViewById(R.id.rb_rate)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")
        movieTitle = bundle?.getString("movieName")

        tv_movieName.text = movieTitle
        rateMovie()
        checkPressed(movieId!!)
        deleteRating(movieId!!)
        configureBackButton(view)
    }

    private fun configureBackButton(view: View) {
        val back: ImageView = view.findViewById(R.id.back)
        back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun checkPressed(id: Int) {
        rateButton.setOnClickListener {
            val rating = myRate.text.toString().toInt()
            movieDetailsViewModel.rateMovie(id, rating)
            Toast.makeText(requireContext(), rating.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun deleteRating(id: Int) {
        deleteRating.setOnClickListener {
            movieDetailsViewModel.deleteRating(id)
            Toast.makeText(requireContext(), "Rating deleted", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun rateMovie() {
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                myRate.text = p1.toInt().toString()
            }
    }
}