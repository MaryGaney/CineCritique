package com.example.cinecritique

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class IndMovie : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ind_movie)

        // Retrieve data passed via Intent
        val movieTitle = intent.getStringExtra("MOVIE_TITLE")
        val movieDescription = intent.getStringExtra("MOVIE_DESCRIPTION")
        val posterUrl = intent.getStringExtra("POSTER_URL")

        // Populate UI
        findViewById<TextView>(R.id.titleText).text = movieTitle
        findViewById<TextView>(R.id.movieDescription).text = movieDescription

        Glide.with(this)
            .load(posterUrl)
            .into(findViewById<ImageView>(R.id.moviePoster))
    }
}