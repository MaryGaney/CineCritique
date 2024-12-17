package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.chromium.net.CronetEngine
import java.time.LocalDate
import java.util.concurrent.Executors

class MovieGenre : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var genreTitleTextView: TextView
    private lateinit var cronetEngine: CronetEngine
    private lateinit var database: CollectionReference

    private var genreName: String? = null
    private var genreId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_genre)

        recyclerView = findViewById(R.id.genreRecycle)
        genreTitleTextView = findViewById(R.id.genreTitle)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MovieAdapterCards(emptyList()) { movie ->
            val intent = Intent(this, IndMovie::class.java).apply {
                putExtra("MOVIE_NAME", movie.title)
                putExtra("MOVIE_POSTER", movie.poster_path)
                putExtra("MOVIE_DESC", movie.overview)
            }
            startActivity(intent)
        }

        //profile pic image takes you to user page
        val pfp: ImageView = findViewById(R.id.pfp)
        //val pf: ImageView = findViewById(R.id.imageView)
        pfp.setOnClickListener {
            val intent = Intent(this, UserPage::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.mainpagebtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        cronetEngine = CronetEngine.Builder(this).build()
        val executor = Executors.newSingleThreadExecutor()
        val webHelper = WebHelper(cronetEngine, executor)

        // Get genre data from the intent
        genreName = intent.getStringExtra("GENRE_NAME")
        genreId = getGenreIdFromName(genreName)

        genreTitleTextView.text = genreName

        // Fetch movies based on genre
        genreId?.let {
            fetchMovies(genreName!!, it) { movies ->
                // Update Firestore with the genre and movie data
                val currentDate = LocalDate.now().toString()
                database = FirebaseFirestore.getInstance().collection("dates")

                val movieData = mapOf(
                    genreName!! to movies.map { movie ->
                        mapOf(
                            "title" to movie.title,
                            "overview" to movie.overview,
                            "poster_path" to movie.poster_path
                        )
                    }
                )


                database.document(currentDate)
                    .set(movieData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.i("HELP", "$genreName movies added successfully!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("HELP", "Failed to add $genreName movies: ${e.message}")
                    }

                // Set up the RecyclerView with movie data
                (recyclerView.adapter as MovieAdapterCards).updateMovies(movies)
            }
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

    private fun fetchMovies(genreName: String, genreNumber: String, callback: (List<Movie>) -> Unit) {
        val url = when (genreName) {
            "Popular" -> "https://api.themoviedb.org/3/trending/movie/day?language=en-US"
            "New" -> "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_release_type=1"
            else -> "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_genres=$genreNumber"
        }

        val headers = mapOf(
            "accept" to "application/json",
            "Authorization" to "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3Y2U4YzlmMDFlNDNhZDU5NTUzZmNjYmFlZmY4MGJmYyIsIm5iZiI6MTczMzE4MTk4OS4zNzYsInN1YiI6IjY3NGU0MjI1YWE4NDRkYzZlZTk0NDZlZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.CIjbdscpuNHNNHGzT2-eM7JR21RmUgJ_A-V2AgrKdwk"
        )

        val executor = Executors.newSingleThreadExecutor()
        val webHelper = WebHelper(cronetEngine, executor)

        webHelper.get(url, headers) { jsonResponse ->
            if (jsonResponse != null) {
                try {
                    val gson = Gson()
                    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
                    val results = jsonObject.getAsJsonArray("results")

                    val movieType = object : TypeToken<List<Movie>>() {}.type
                    val movies: List<Movie> = gson.fromJson(results, movieType)

                    callback(movies)
                } catch (e: Exception) {
                    Log.e("JSON_ERROR", "Failed to parse JSON: ${e.message}")
                }
            } else {
                Log.e("HTTP_ERROR", "Failed to fetch data.")
            }
        }
    }

    private fun getGenreIdFromName(genreName: String?): String {
        return when (genreName) {
            "Popular" -> "0"
            "Action" -> "27"
            "Adventure" -> "12"
            "Animation" -> "16"
            "Comedy" -> "35"
            "Documentary" -> "99"
            "Drama" -> "18"
            "Family" -> "10751"
            "Fantasy" -> "14"
            "Horror" -> "27"
            "Mystery" -> "9648"
            "Romance" -> "10749"
            "Science Fiction" -> "878"
            "Thriller" -> "53"
            "War" -> "10752"
            "Western" -> "37"
            else -> ""
        }
    }
}
