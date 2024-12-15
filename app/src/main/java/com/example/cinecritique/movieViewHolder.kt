package com.example.cinecritique

import androidx.recyclerview.widget.RecyclerView
import com.example.cinecritique.databinding.ActivityIndMovieBinding
class movieViewHolder(
    private val binding: ActivityIndMovieBinding,
    private val onMovieClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        //basically like holding the task item in place so we can copy it over
        fun bindTaskItem(movieTitle: String, position: Int) {
            binding.movieTitle.text = movieTitle
            binding.root.setOnClickListener {
                onMovieClicked(position)
            }
        }
    }
