package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class IndMovie : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ind_movie)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navigate to user page
        findViewById<ImageView>(R.id.pfp).setOnClickListener {
            startActivity(Intent(this, UserPage::class.java))
        }

        // Navigate to main page
        findViewById<ImageView>(R.id.mainpagebtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Get movie details
        val movieTitle = intent.getStringExtra("MOVIE_NAME") ?: ""
        val movieDescription = intent.getStringExtra("MOVIE_DESC") ?: ""
        val posterUrl = intent.getStringExtra("MOVIE_POSTER") ?: ""

        // Set movie details in UI
        findViewById<TextView>(R.id.movieTitle).text = movieTitle
        findViewById<TextView>(R.id.movieDescription).text = movieDescription
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${posterUrl}")
            .into(findViewById<ImageView>(R.id.moviePoster))

        val ratingButtons = listOf(
            findViewById<MaterialButton>(R.id.ratingButton1),
            findViewById<MaterialButton>(R.id.ratingButton2),
            findViewById<MaterialButton>(R.id.ratingButton3),
            findViewById<MaterialButton>(R.id.ratingButton4),
            findViewById<MaterialButton>(R.id.ratingButton5)
        )
        val removeRatingButton = findViewById<Button>(R.id.removeRating)

        // Initialize rating functionality
        initRatingButtons(ratingButtons, removeRatingButton)

        // Load user rating and initialize the "Remove Rating" button
        FirebaseAuth.getInstance().currentUser?.let { user ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                        val movieRating = allRatings?.get(movieTitle)?.get("rating") as? Long
                        if (movieRating != null) {
                            updateRatingUI(movieRating.toInt(), ratingButtons, removeRatingButton)
                        }
                    }
                }
        }
    }

    private fun initRatingButtons(
        buttons: List<MaterialButton>,
        removeRatingButton: Button
    ) {
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val rating = index + 1
                val movie = Movie(
                    title = intent.getStringExtra("MOVIE_NAME") ?: "",
                    poster_path = intent.getStringExtra("MOVIE_POSTER") ?: "",
                    overview = intent.getStringExtra("MOVIE_DESC") ?: ""
                )
                rateMovie(movie, rating)
                updateRatingUI(rating, buttons, removeRatingButton)
            }
        }

        // Remove rating functionality
        removeRatingButton.setOnClickListener {
            val movie = Movie(
                title = intent.getStringExtra("MOVIE_NAME") ?: "",
                poster_path = intent.getStringExtra("MOVIE_POSTER") ?: "",
                overview = intent.getStringExtra("MOVIE_DESC") ?: ""
            )
            removeMovieRating(movie, buttons, removeRatingButton)
        }
    }

    private fun updateRatingUI(
        rating: Int,
        buttons: List<MaterialButton>,
        removeRatingButton: Button
    ) {
        buttons.forEachIndexed { index, button ->
            button.setIconResource(
                if (index < rating) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24
            )
        }
        removeRatingButton.visibility = Button.VISIBLE
    }

    private fun resetRatingUI(buttons: List<MaterialButton>, removeRatingButton: Button) {
        buttons.forEach { button ->
            button.setIconResource(R.drawable.baseline_star_border_24)
        }
        removeRatingButton.visibility = Button.INVISIBLE
    }

    private fun rateMovie(movie: Movie, rating: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                    val movieKey = movie.title
                    val prevRating = allRatings?.get(movieKey)?.get("rating") as? Long ?: 0L

                    // Remove previous rating
                    if (prevRating > 0) {
                        userDocRef.update("${prevRating}Star", FieldValue.arrayRemove(allRatings?.get(movieKey)))
                    }

                    // Add new rating
                    val movieData = mapOf(
                        "title" to movie.title,
                        "poster_path" to movie.poster_path,
                        "overview" to movie.overview,
                        "rating" to rating
                    )
                    userDocRef.update("AllRatings.$movieKey", movieData)
                    userDocRef.update("${rating}Star", FieldValue.arrayUnion(movieData))
                }
            }
        }
    }

    private fun removeMovieRating(
        movie: Movie,
        buttons: List<MaterialButton>,
        removeRatingButton: Button
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                    val movieKey = movie.title
                    val movieData = allRatings?.get(movieKey)

                    movieData?.let {
                        val rating = (movieData["rating"] as? Long)?.toInt() ?: 0
                        userDocRef.update("${rating}Star", FieldValue.arrayRemove(movieData))
                        userDocRef.update("AllRatings.$movieKey", FieldValue.delete())
                            .addOnSuccessListener {
                                resetRatingUI(buttons, removeRatingButton)
                                Toast.makeText(this, "Rating removed!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }
}
