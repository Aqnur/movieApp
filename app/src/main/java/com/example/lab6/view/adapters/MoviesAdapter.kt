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

class MoviesAdapter(
    private val itemClickListner: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var moviePosition = 1

    private var movies = listOf<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> MoviesViewHolder(
                inflater.inflate(R.layout.movie_item, parent, false)
            )
            VIEW_TYPE_LOADING -> LoaderViewHolder(
                inflater.inflate(R.layout.loader_layout, parent, false)
            )
            else -> throw Throwable("Invalid View!")
        }
    }

    override fun getItemCount(): Int = movies.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoviesViewHolder) {
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

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if(position == movies.size - 1) VIEW_TYPE_LOADING
            else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }


    fun addFooterLoading() {
        isLoaderVisible = true
        (movies as? ArrayList<Result>)?.add(Result(id = -1))
        notifyItemInserted(movies.size.minus(1))
    }

    fun removeFooterLoading() {
        isLoaderVisible = false
        val position = movies.size.minus(1)
        if (movies.isNotEmpty()) {
            val item = getItem(position)
            if (item != null) {
                (movies as? ArrayList<Result>)?.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }

    private fun getItem(position: Int): Result? {
        return movies[position]
    }

    fun addItems(moviesList: List<Result>) {
        if (movies.isEmpty()) movies = moviesList
        else {
            if (movies[movies.size - 1] != moviesList[moviesList.size - 1])
                (movies as? ArrayList<Result>)?.addAll(moviesList)
        }
        notifyDataSetChanged()
    }

    fun updateItem(movie: Result) {
        val id = movie.id
        val isClicked = movie.liked
        val foundMovie = movies.find { it.id == id }
        foundMovie?.liked = isClicked
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movies as? ArrayList<Result>)?.clear()
        moviePosition = 1
        notifyDataSetChanged()
    }

    inner class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.moviePhoto)
        private val title: TextView = itemView.findViewById(R.id.originalTitle)
        private val rusTitle: TextView = itemView.findViewById(R.id.rusTitle)
        private val rating: TextView = itemView.findViewById(R.id.movieRating)
        private val votes: TextView = itemView.findViewById(R.id.movieVotes)
        private val movieId: TextView = itemView.findViewById(R.id.movieId)
        private val moviesLike: ImageView = itemView.findViewById(R.id.moviesLike)
        private var id: Int = 0

        fun bind(movie: Result) {
            if (movie != null) {

                if (movie.position == 0) {
                    movie.position = moviePosition
                    moviePosition++
                }

                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                    .into(photo)

                id = movie.id
                movieId.text = movie.position.toString()
                title.text = movie.title
                rusTitle.text = movie.originalTitle + "(" + movie.releaseDate.substring(
                    0,
                    movie.releaseDate.length - 6
                ) + ")"
                rating.text = "Рейтинг: " + movie.voteAverage.toString()
                votes.text = "Голоса: " + movie.voteCount.toString()

                if (movie.liked == 0 || movie.liked == 10) {
                    moviesLike.setImageResource(R.drawable.ic_like)
                } else {
                    moviesLike.setImageResource(R.drawable.ic_lliked)
                }

                moviesLike.setOnClickListener {
                    itemClickListner?.sharedView(movie)
                    val drawable: Drawable = moviesLike.drawable
                    if (drawable.constantState?.equals(
                            getDrawable(
                                itemView.context,
                                R.drawable.ic_like
                            )?.constantState
                        ) == true
                    ) {
                        itemClickListner?.addToFavourites(true, adapterPosition, movie)
                        moviesLike.setImageResource(R.drawable.ic_lliked)
                    } else {
                        itemClickListner?.addToFavourites(false, adapterPosition, movie)
                        moviesLike.setImageResource(R.drawable.ic_like)
                    }
                }
            }
        }
    }

    inner class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface RecyclerViewItemClick {
        fun addToFavourites(boolean: Boolean, position: Int, item: Result)
        fun sharedView(item: Result)
    }
}