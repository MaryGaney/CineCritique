package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore

class UserPage : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //logo takes you to main page
        val mainpagebtn: ImageView = findViewById(R.id.mainpagebtn)
        mainpagebtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()

        )



        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    Log.i("MYTAG", "user logged out")
                }
            signInLauncher.launch(signInIntent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.oneStarsRecycle)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Initialize your adapter with an empty list first
        val movieList = mutableListOf<Movie>()
        val adapter = MovieAdapter(movieList) { movie ->
            val intent = Intent(this, IndMovie::class.java).apply {
                putExtra("MOVIE_NAME", movie.title)
                putExtra("MOVIE_POSTER", movie.poster_path)
                putExtra("MOVIE_DESC", movie.overview)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            // Set the profile picture (either from FirebaseAuth or a default one)
            val userpic: ImageView = findViewById(R.id.userpic)
            user.photoUrl?.let { photoUrl ->
                // If the user has a photo URL, load it into the ImageView
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop() // Optional: to make the image circular
                    .into(userpic)
            } ?: run {
                // If the user does not have a profile picture, use a default image
                userpic.setImageResource(R.drawable.baseline_person_24)
            }

            // Set the username (either from FirebaseAuth or Firestore)
            val usernameTextView: TextView = findViewById(R.id.username)
            user.displayName?.let { displayName ->
                // If the user has a display name, use it
                usernameTextView.text = displayName
            } ?: run {
                // If the user does not have a display name, fetch from Firestore
                userDocRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Username"
                        usernameTextView.text = username
                    } else {
                        // If the document does not exist or the username is not found, fallback to default
                        usernameTextView.text = "Username"
                    }
                }
            }

            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Assuming the movie data is under the "1Star" field
                    val movies = document.get("1Star") as? List<Map<String, Any>> ?: emptyList()
                    val movieList = movies.map { map ->
                        Movie(
                            title = map["title"] as String,
                            overview = map["overview"] as String,
                            poster_path = map["poster_path"] as String
                        )
                    }
                    (recyclerView.adapter as MovieAdapter).updateMovies(movieList)
                }
            }
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

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            //successful sign in
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                //do something here
                Log.i("MYTAG", "signed in")
            }
        } else {
            Log.i("MYTAG", "failed sign in")
        }
    }
}