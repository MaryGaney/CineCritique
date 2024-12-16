package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
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
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        //signInLauncher.launch(signInIntent)

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Log.i("MYTAG","user logged out")
                }
            signInLauncher.launch(signInIntent)
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