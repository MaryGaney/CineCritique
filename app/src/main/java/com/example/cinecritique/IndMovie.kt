package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

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
            startActivity(intent)
        }

        // Retrieve data passed via Intent
        val movieTitle = intent.getStringExtra("MOVIE_NAME")
        val movieDescription = intent.getStringExtra("MOVIE_DESC")
        val posterUrl = intent.getStringExtra("MOVIE_POSTER")

        // Populate UI
        findViewById<TextView>(R.id.movieTitle).text = movieTitle
        findViewById<TextView>(R.id.movieDescription).text = movieDescription

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${posterUrl}")
            .into(findViewById<ImageView>(R.id.moviePoster))

        val button1 = findViewById<MaterialButton>(R.id.ratingButton1)
        val button2 = findViewById<MaterialButton>(R.id.ratingButton2)
        val button3 = findViewById<MaterialButton>(R.id.ratingButton3)
        val button4 = findViewById<MaterialButton>(R.id.ratingButton4)
        val button5 = findViewById<MaterialButton>(R.id.ratingButton5)

        // Initialize the rating bar functionality
        showRatingBar(button1, button2, button3, button4, button5)

    }

    private fun showRatingBar(
        mButton1: MaterialButton,
        mButton2: MaterialButton,
        mButton3: MaterialButton,
        mButton4: MaterialButton,
        mButton5: MaterialButton
    ) {
        mButton5.setOnClickListener {
            setStarStates(mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5), 5)
        }

        mButton4.setOnClickListener {
            setStarStates(mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5), 4)
        }

        mButton3.setOnClickListener {
            setStarStates(mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5), 3)
        }

        mButton2.setOnClickListener {
            setStarStates(mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5), 2)
        }

        mButton1.setOnClickListener {
            setStarStates(mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5), 1)
        }
    }

    private fun setStarStates(buttons: MutableList<MaterialButton>, rating: Int) {
        buttons.forEachIndexed { index, button ->
            if (index < rating) {
                button.setIconResource(R.drawable.baseline_star_24) // Full star
            } else {
                button.setIconResource(R.drawable.baseline_star_border_24) // Empty star
            }
        }

        Toast.makeText(this, "Rating: $rating", Toast.LENGTH_SHORT).show()
    }
}