package com.example.cinecritique

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import java.time.LocalDate
import java.util.Date


class MainActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    companion object {
        var userId: String? = null
        private const val ADD_TASK_REQUEST_CODE = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var database: CollectionReference

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

        //sign in stuff
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()
        signInLauncher.launch(signInIntent)

        //profile pic image takes you to user page
        val pfp: ImageView = findViewById(R.id.pfp)
        //val pf: ImageView = findViewById(R.id.imageView)
        pfp.setOnClickListener {
            val intent = Intent(this, UserPage::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        cronetEngine = CronetEngine.Builder(this).build()
        val curDate = LocalDate.now().toString()

        val db = FirebaseFirestore.getInstance()
        database = db.collection("dates")

        val movieGenre = mutableMapOf<String,String>()
        movieGenre.putAll(mapOf("Popular" to "0","Action" to "27", "Adventure" to "12", "Animation" to "16",
            "Comedy" to "35", "Crime" to "80", "Documentary" to "99", "Drama" to "18", "Family" to "10751",
            "Fantasy" to "14", "History" to "36", "Horror" to "27", "Music" to "10402", "Mystery" to "9648",
            "Romance" to "10749", "Science Fiction" to "878", "TV Movie" to "10770", "Thriller" to "53",
            "War" to "10752", "Western" to "37"))

        database.document(curDate).get().addOnSuccessListener { document ->
            if (!document.exists()) {
                database.document(curDate).set(emptyMap<String, Any>()) // Add the date document
            }
            // Fetch movie genres and set up the RecyclerViews
            setupGenreRecyclerViews(curDate)
        }.addOnFailureListener { e ->
            Log.e("FIREBASE_ERROR", "Failed to fetch date document: ${e.message}")
        }
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

    private fun setupAdapter(recyclerView: RecyclerView, movies: List<Movie>) {
        val adapter = MovieAdapter(movies) { movie ->
            val intent = Intent(this, IndMovie::class.java).apply {
                putExtra("MOVIE_NAME", movie.title)
                putExtra("MOVIE_POSTER", movie.poster_path)
                putExtra("MOVIE_DESC", movie.overview)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setupGenreRecyclerViews(dateKey: String) {
        val movieGenres = mapOf(
            "Popular" to "0",
            "Action" to "27",
            "Comedy" to "35",
            "New" to "0"
        )

        movieGenres.forEach { (genreName, genreId) ->
            val recyclerViewId = when (genreName) {
                "Popular" -> R.id.popularRecycle
                "Action" -> R.id.actionRecycle
                "Comedy" -> R.id.comedyRecycle
                "New" -> R.id.newReleaseRecycle
                else -> null
            }

            recyclerViewId?.let { id ->
                val recyclerView: RecyclerView = findViewById(id)
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = MovieAdapter(emptyList()) { } // Set an empty adapter initially

                Log.i("MYTAG",database.document(dateKey).get().toString() + "beforeOnSuccess");
                database.document(dateKey).get().addOnSuccessListener{ document ->
                    val moviesForGenre = document.get(genreName) as? List<Map<String, Any>>
                    Log.i("MYTAG",document.get(genreName).toString());
                    if (moviesForGenre != null) {
                        val movies = moviesForGenre.map { map ->
                            Movie(
                                title = map["title"] as String,
                                overview = map["overview"] as String,
                                poster_path = map["poster_path"] as String
                            )
                        }

                        (recyclerView.adapter as MovieAdapter).updateMovies(movies)
                        setupAdapter(recyclerView, movies)
                    } else {
                        fetchMovies(genreName, genreId) { movies ->
                            val movieData = mapOf(
                                genreName to movies.map { movie ->
                                    mapOf(
                                        "title" to movie.title,
                                        "overview" to movie.overview,
                                        "poster_path" to movie.poster_path
                                    )
                                }
                            )

                            database.document(dateKey)
                                .set(movieData, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.i("FIREBASE", "$genreName movies added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FIREBASE_ERROR", "Failed to add $genreName movies: ${e.message}")
                                }

                            (recyclerView.adapter as MovieAdapter).updateMovies(movies)
                            setupAdapter(recyclerView, movies)
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e("FIREBASE_ERROR", "Failed to fetch genre data: ${e.message}")
                }
            }
        }
    }

    private fun fetchMovies(genreName: String, genreNumber: String, callback: (List<Movie>) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        val webHelper = WebHelper(cronetEngine, executor)

        val url = when (genreName) {
            "Popular" -> "https://api.themoviedb.org/3/trending/movie/day?language=en-US"
            "New" -> "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_release_type=1"
            else -> "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_genres=$genreNumber"
        }

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

                    callback(movies) // Return fetched movies via callback
                } catch (e: Exception) {
                    Log.e("JSON_ERROR", "Failed to parse JSON: ${e.message}")
                }
            } else {
                Log.e("HTTP_ERROR", "Failed to fetch data.")
            }
        }
    }
}
