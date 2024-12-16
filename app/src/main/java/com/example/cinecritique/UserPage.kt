package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display user ratings
        displayUserRatings()

    }

    private fun displayUserRatings() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    (1..5).forEach { rating ->
                        val ratingKey = "${rating}Star"
                        val movies = document.get(ratingKey) as? List<Map<String, Any>>
                        if (movies != null) {
                            updateRatingList(rating, movies)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRatingList(rating: Int, movies: List<Map<String, Any>>) {
        val recyclerView = findViewById<RecyclerView>(getRecyclerViewIdForRating(rating))
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val movieList = movies.map { map ->
            Movie(
                title = map["title"] as String,
                overview = map["overview"] as String,
                poster_path = map["poster_path"] as String
            )
        }

        val adapter = MovieAdapter(movieList) { movie ->
            val intent = Intent(this, IndMovie::class.java).apply {
                putExtra("MOVIE_NAME", movie.title)
                putExtra("MOVIE_POSTER", movie.poster_path)
                putExtra("MOVIE_DESC", movie.overview)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }

    private fun getRecyclerViewIdForRating(rating: Int): Int {
        return when (rating) {
            1 -> R.id.oneStarsRecycle
            2 -> R.id.twoStarsRecycle
            3 -> R.id.threeStarsRecycle
            4 -> R.id.fourStarsRecycle
            5 -> R.id.fiveStarsRecycle
            else -> throw IllegalArgumentException("Invalid rating")
        }
    }
}