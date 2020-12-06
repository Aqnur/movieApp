package com.example.lab6.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.data.model.movie.Result

class MediaListAdapter(
    private val itemClickListener: RecyclerViewItemClick? = null,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = listOf<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movies_item, parent, false)
        return MediaListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (movies.size > 10) {
            10
        } else {
            movies.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MediaListViewHolder) {
            return holder.bind(movies[position])
        }
    }

    fun addItems(moviesList: List<Result>) {
        movies = moviesList
        notifyDataSetChanged()
    }

    fun updateItem(movie: Result) {
        val foundMovie = movies.find { it.id == movie.id }
        foundMovie?.liked = movie.liked
        notifyDataSetChanged()
    }

    fun clearAll() {
        (movies as? ArrayList<Result>)?.clear()
        notifyDataSetChanged()
    }

    inner class MediaListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.iv_posterImage)
        private val title: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(movie : Result) {
            title.text = movie.originalTitle
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(img)

            itemView.setOnClickListener {
                itemClickListener?.itemClick(adapterPosition, movie)
            }
        }

    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Result)
    }

}