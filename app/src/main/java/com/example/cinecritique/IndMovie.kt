package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //profile pic image takes you to user page
        val pfp: ImageView = findViewById(R.id.pfp)
        //val pf: ImageView = findViewById(R.id.imageView)
        pfp.setOnClickListener {
            val intent = Intent(this, UserPage::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

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