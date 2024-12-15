package com.example.cinecritique

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.Strictness
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var database: CollectionReference
    private lateinit var curdate : Date

    private lateinit var cronetEngine: CronetEngine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize CronetEngine
        cronetEngine = CronetEngine.Builder(this).build()
        val movieGenre = mutableMapOf<String,String>()
        movieGenre.putAll(mapOf("Action" to "27", "Adventure" to "12", "Animation" to "16",
            "Comedy" to "35", "Crime" to "80", "Documentary" to "99", "Drama" to "18", "Family" to "10751",
            "Fantasy" to "14", "History" to "36", "Horror" to "27", "Music" to "10402", "Mystery" to "9648",
            "Romance" to "10749", "Science Fiction" to "878", "TV Movie" to "10770", "Thriller" to "53",
            "War" to "10752", "Western" to "37"))


        // Firestore initialization
        val db = FirebaseFirestore.getInstance()
        val dateKey = curdate.toString() // Format if needed (e.g., SimpleDateFormat)
        database = db.collection("dates")

        // Check if the document for the current date exists
        database.document(dateKey).get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Create a new document with today's date
                db.collection("dates").document(dateKey).set(emptyMap<String, Any>())
            }
        }

        fetchMovies("Action", movieGenre["Action"].toString()) { actionMovies ->
            if (actionMovies.isNotEmpty()) {
                // Add to Firestore under the current date
                val dateKey = curdate.toString() // Format this if needed
                val actionGenreData = hashMapOf("Action" to actionMovies)

                database.document(dateKey)
                    .set(actionGenreData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.i("DATABASE", "Movies added successfully!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DATABASE_ERROR", "Failed to add movies: ${e.message}")
                    }

                // Update RecyclerView with movies
                adapter = MovieAdapter(actionMovies){ position ->
                    val intent = Intent(this, IndMovie::class.java).apply{
                        putExtra("MOVIE_NAME", actionMovies.get(position).title)
                        putExtra("MOVIE_POSTER", actionMovies.get(position).poster_path)
                        putExtra("MOVIE_DESC", actionMovies.get(position).overview)
                    }
                    startActivity(intent)
                }
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No movies found for Action genre.", Toast.LENGTH_SHORT).show()
            }
        }


        val myList : RecyclerView  = findViewById(R.id.actionRecycle);
        myList.setLayoutManager(layoutManager);
//
//        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
//        Glide.with(this)
//            .load(imageUrl)
//            .into(imageView)

//        val txt1 : TextView = findViewById(R.id.testText)
//        txt1.setText(response.body().toString())
        /*

         */
    }

    private fun fetchMovies(genreName : String, genreNumber : String, callback: (List<Movie>) -> Unit){
        val executor = Executors.newSingleThreadExecutor()
        val webHelper = WebHelper(cronetEngine, executor)

        val url = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_genres=27"
        val headers = mapOf(
            "accept" to "application/json",
            "Authorization" to "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3Y2U4YzlmMDFlNDNhZDU5NTUzZmNjYmFlZmY4MGJmYyIsIm5iZiI6MTczMzE4MTk4OS4zNzYsInN1YiI6IjY3NGU0MjI1YWE4NDRkYzZlZTk0NDZlZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.CIjbdscpuNHNNHGzT2-eM7JR21RmUgJ_A-V2AgrKdwk"
        )
        webHelper.get(url, headers) { jsonResponse ->
            if (jsonResponse != null) {
                try {
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
                    val results = jsonObject.getAsJsonArray("results")

                    val movieType = object : TypeToken<List<Movie>>() {}.type
                    val movies: List<Movie> = gson.fromJson(results, movieType)

                    // Process movies (e.g., display or store)
                    movies.forEach { movie ->
                        Log.i("MOVIE", "Title: ${movie.title}, Overview: ${movie.overview}, Poster: ${movie.poster_path}")
                    }
                    //method is async so have to use callback here
                        //returns empty list before callback is complete so tell want movies returned
                    callback(movies);
                } catch (e: Exception) {
                    Log.e("JSON_ERROR", "Failed to parse JSON: ${e.message}")
                }
            } else {
                Log.e("HTTP_ERROR", "Failed to fetch data.")
            }
        }
    }
}
