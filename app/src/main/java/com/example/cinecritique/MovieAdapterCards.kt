package com.example.cinecritique

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapterCards(
    private var movies: List<Movie>,
    private val clickListener: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapterCards.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.moviePoster)
        private val titleTextView: TextView = itemView.findViewById(R.id.movieTitle)
        private val overviewTextView: TextView = itemView.findViewById(R.id.movieOverview)

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(imageView)

            titleTextView.text = movie.title ?: "No Title"
            overviewTextView.text = movie.overview ?: "No Description"

            itemView.setOnClickListener { clickListener(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Inflate the new movie_card layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        Log.i("HELP", "Binding movie at position $position: ${movies[position].title}")
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}
