package com.example.lab6.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.data.model.movie.Result
import de.hdodenhof.circleimageview.CircleImageView

class ShortCastAdapter(
    val itemClickListener: RecyclerViewItemClick? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var casts = listOf<Cast>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cast_item, parent, false)
        return ShortCastViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (casts.size > 3) {
            3
        } else {
            casts.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ShortCastViewHolder) {
            return holder.bind(casts[position])
        }
    }

    fun addItems(castList: List<Cast>) {
        casts = castList
        notifyDataSetChanged()
    }

    inner class ShortCastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val castImage: CircleImageView = itemView.findViewById(R.id.iv_actor)
        private val castName: TextView = itemView.findViewById(R.id.tv_actorName)
        private val castRole: TextView = itemView.findViewById(R.id.tv_actorRoleName)

        fun bind(cast: Cast) {
            castName.text = cast.name
            castRole.text = cast.character

            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w342${cast.profile_path}")
                .into(castImage)

            itemView.setOnClickListener {
                itemClickListener?.itemClick(adapterPosition, cast)
            }
        }

    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Cast)
    }
}