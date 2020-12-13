package com.example.lab6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.lab6.R
import com.example.lab6.data.model.cast.Cast
import com.example.lab6.view_model.MovieDetailViewModel
import kotlinx.android.synthetic.main.actor_detail.*
import kotlinx.android.synthetic.main.cast_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActorFragment : Fragment() {
    private val movieDetailViewModel by viewModel<MovieDetailViewModel>()

    private var actorId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.actor_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        actorId = bundle?.getInt("id")

        getActor(actorId!!)
        configureBackButton(view)
    }

    private fun getActor(id: Int) {
        movieDetailViewModel.getActor(id)
        movieDetailViewModel.liveData.observe(requireActivity(), Observer { result ->
            when(result) {
                is MovieDetailViewModel.State.ShowLoading -> {
                    srl_actor.isRefreshing = true
                }
                is MovieDetailViewModel.State.HideLoading -> {
                    srl_actor.isRefreshing = false
                }
                is MovieDetailViewModel.State.Actor -> {
                    setData(result.actor!!)
                }
            }
        })
    }

    private fun setData(cast: Cast) {

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${cast.profile_path}")
            .into(iv_actorImage)

        tv_actorName.text = cast.name
        tv_actorBirthday.text = cast.birthday
        tv_actorPlaceOfBirth.text = cast.place_of_birth
        if(cast.gender == 2) {
            tv_actorGender.text = "Male"
        } else {
            tv_actorGender.text = "Female"
        }
        tv_actorKnownFor.text = "Known for: " + cast.known_for_department
        tv_actorAlsoKnownFor.text = "Also known as: " + cast.also_known_as.toString().substring(1, cast.also_known_as.toString().length - 1)
        tv_actorBiography.text =  cast.biography

    }

    private fun configureBackButton(view: View) {
        val back: ImageView = view.findViewById(R.id.back)
        back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}