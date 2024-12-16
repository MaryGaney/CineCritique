package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.CollectionReference
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.firestore.FieldValue

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

        //logo takes you to main page
        val mainpagebtn: ImageView = findViewById(R.id.mainpagebtn)
        mainpagebtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
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

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                    val movieKey = movieTitle // Or use a unique identifier like movie.id
                    val movieRating = allRatings?.get(movieKey)?.get("rating") as? Long
                    movieRating?.let {
                        // Set the rating UI based on the user's rating
                        showRatingOnUI(it.toInt()) // Create a function that sets the UI based on the rating
                    }
                }
            }
        }
    }
        private fun showRatingOnUI(rating: Int) {
            val mButton1 = findViewById<MaterialButton>(R.id.ratingButton1)
            val mButton2 = findViewById<MaterialButton>(R.id.ratingButton2)
            val mButton3 = findViewById<MaterialButton>(R.id.ratingButton3)
            val mButton4 = findViewById<MaterialButton>(R.id.ratingButton4)
            val mButton5 = findViewById<MaterialButton>(R.id.ratingButton5)

            val buttons = mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5)
            setStarStates(buttons, rating)
        }


    @OptIn(UnstableApi::class)
    private fun rateMovie(movie: Movie, rating: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get all existing ratings from the document
                    val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                    val movieKey = movie.title // or use a unique identifier like movie.id

                    // Remove the movie from any previous rating list (if it exists)
                    allRatings?.get(movieKey)?.let { existingMovie ->
                        val prevRating = (existingMovie["rating"] as? Long)?.toInt() ?: 0
                        userDocRef.update("${prevRating}Star", FieldValue.arrayRemove(existingMovie)) // Remove from previous rating
                    }

                    // Remove from AllRatings
                    userDocRef.update("AllRatings.$movieKey", FieldValue.delete())

                    // Add the movie data to the new rating list (new rating)
                    val movieData = mapOf(
                        "title" to movie.title,
                        "poster_path" to movie.poster_path,
                        "overview" to movie.overview,
                        "rating" to rating
                    )

                    // Update AllRatings with the new rating
                    userDocRef.update("AllRatings.$movieKey", movieData)

                    // Add movie to the new star rating list
                    userDocRef.update("${rating}Star", FieldValue.arrayUnion(movieData))
                        .addOnSuccessListener { Log.i("FIREBASE", "Movie rated successfully") }
                        .addOnFailureListener { e -> Log.e("FIREBASE_ERROR", "Failed to rate movie: ${e.message}") }
                }
            }
        }
    }


    @OptIn(UnstableApi::class)
    private fun removeRating(movie: Movie) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val allRatings = document.get("AllRatings") as? Map<String, Map<String, Any>>
                    val movieKey = movie.title // or use a unique identifier like movie.id

                    allRatings?.get(movieKey)?.let { movieData ->
                        val rating = (movieData["rating"] as? Long)?.toInt() ?: 0
                        userDocRef.update("${rating}Star", FieldValue.arrayRemove(movieData))
                        userDocRef.update("AllRatings.$movieKey", FieldValue.delete())
                            .addOnSuccessListener { Log.i("FIREBASE", "Movie removed successfully") }
                            .addOnFailureListener { e -> Log.e("FIREBASE_ERROR", "Failed to remove movie: ${e.message}") }
                    }
                }
            }
        }
    }



    private fun showRatingBar(
        mButton1: MaterialButton,
        mButton2: MaterialButton,
        mButton3: MaterialButton,
        mButton4: MaterialButton,
        mButton5: MaterialButton
    ) {
        val buttons = mutableListOf(mButton1, mButton2, mButton3, mButton4, mButton5)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val selectedRating = index + 1 // Convert index to rating (1-based)
                setStarStates(buttons, selectedRating)

                // Call rateMovie to update Firebase
                val movie = Movie(
                    title = intent.getStringExtra("MOVIE_NAME") ?: "",
                    poster_path = intent.getStringExtra("MOVIE_POSTER") ?: "",
                    overview = intent.getStringExtra("MOVIE_DESC") ?: ""
                )
                if (movie.title.isNotEmpty()) {
                    rateMovie(movie, selectedRating)
                } else {
                    Toast.makeText(this, "Movie information is missing!", Toast.LENGTH_SHORT).show()
                }
            }
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