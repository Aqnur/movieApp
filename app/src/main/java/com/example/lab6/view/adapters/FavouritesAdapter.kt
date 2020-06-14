package com.example.lab6.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.model.json.movie.Result
import com.example.lab6.view.activites.MovieDetailActivity

class FavouritesAdapter(
    private val itemClickListner: RecyclerViewItemClick? = null,
    val movies: List<Result>,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FavouritesViewHolder) {
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
            rusTitle.text = movie.originalTitle + "(" + movie.releaseDate.substring(0, movie.releaseDate.length - 6) + ")"
            rating.text = "Рейтинг: " + movie.voteAverage.toString()
            votes.text = "Голоса: " + movie.voteCount.toString()

            if (movie.liked == 1 || movie.liked == 11) {
                moviesLike.setImageResource(R.drawable.ic_lliked)
            } else {
                moviesLike.setImageResource(R.drawable.ic_like)
            }

            moviesLike.setOnClickListener {
                itemClickListner?.removeFromFavourites(false, adapterPosition, movie)
                moviesLike.setImageResource(R.drawable.ic_like)
            }
        }
    }

    interface RecyclerViewItemClick {
        fun removeFromFavourites(boolean: Boolean, position: Int, item: Result)
    }
}
