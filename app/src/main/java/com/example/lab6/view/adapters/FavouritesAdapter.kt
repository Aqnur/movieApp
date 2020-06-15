package com.example.lab6.view.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.model.json.movie.Result
import com.example.lab6.view.activites.MovieDetailActivity

class FavouritesAdapter(
    private val itemClickListner: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = listOf<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavouritesViewHolder) {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MovieDetailActivity::class.java).also {
                    it.putExtra("id", movies[position].id)
                    it.putExtra("pos", position)
                }
                context.startActivity(intent)
            }
            return holder.bind(movies[position])
        }
    }

    fun addItems(moviesList: List<Result>) {
        movies = moviesList
        notifyDataSetChanged()
    }

    fun addItem(movie: Result) {
        if (!movies.contains(movie)) {
            (movies as? ArrayList<Result>)?.add(movie)
            notifyItemInserted(movies.size - 1)
        }
    }

    fun removeItem(movie: Result) {
        val id = movie.id
        val foundMovie = movies.find { it.id == id }
        if (foundMovie != null) {
            (movies as? ArrayList<Result>)?.remove(foundMovie)
        }
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movies as? ArrayList<Result>)?.clear()
        notifyDataSetChanged()
    }

    inner class FavouritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.moviePhoto)
        private val title: TextView = itemView.findViewById(R.id.originalTitle)
        private val rusTitle: TextView = itemView.findViewById(R.id.rusTitle)
        private val rating: TextView = itemView.findViewById(R.id.movieRating)
        private val votes: TextView = itemView.findViewById(R.id.movieVotes)
        private val movieId: TextView = itemView.findViewById(R.id.movieId)
        private val moviesLike: ImageView = itemView.findViewById(R.id.moviesLike)
        private var id: Int = 0

        fun bind(movie: Result) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(photo)

            id = movie.id
            movieId.text = (adapterPosition + 1).toString()
            title.text = movie.title
            rusTitle.text = movie.originalTitle + "(" + movie.releaseDate.substring(
                0,
                movie.releaseDate.length - 6
            ) + ")"
            rating.text = "Рейтинг: " + movie.voteAverage.toString()
            votes.text = "Голоса: " + movie.voteCount.toString()

            if (movie.liked) {
                moviesLike.setImageResource(R.drawable.ic_lliked)
            } else {
                moviesLike.setImageResource(R.drawable.ic_like)
            }

            moviesLike.setOnClickListener {
                itemClickListner?.removeFromFavourite(adapterPosition, movie)
                moviesLike.setImageResource(R.drawable.ic_like)
            }
        }
    }

    interface RecyclerViewItemClick {
        fun removeFromFavourite(position: Int, item: Result)
    }
}
