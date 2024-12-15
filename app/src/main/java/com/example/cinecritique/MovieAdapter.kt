package com.example.cinecritique

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinecritique.databinding.ActivityIndMovieBinding

class MovieAdapter(private var movieItems: List<Movie>,
private val onMovieClicked: (Int) -> Unit) : RecyclerView.Adapter<movieViewHolder>() {

        //make the items clickable
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): movieViewHolder {
            val from = LayoutInflater.from(parent.context)
            val binding = ActivityIndMovieBinding.inflate(from, parent, false)
            return movieViewHolder(binding, onMovieClicked)
        }

        //get the amount of task items
        override fun getItemCount(): Int = movieItems.size

        override fun onBindViewHolder(holder: movieViewHolder, position: Int) {
            holder.bindTaskItem(movieItems[position].title, position)
        }
}
