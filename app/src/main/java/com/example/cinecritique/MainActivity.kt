package com.example.cinecritique

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import okhttp3.Request
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//        val myBuilder = CronetEngine.Builder(this)
//        val cronetEngine: CronetEngine = myBuilder.build()
//        val executor: Executor = Executors.newSingleThreadExecutor()
//        val networkClient = WebHelper(cronetEngine, executor)
//
//        networkClient.get("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_genres=27&year=2024") { response ->
//            val gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()
//            val nonPrintableRegex = "[^\\x20-\\x7E]".toRegex()
//            // Replace non-printable characters with an empty string
//            val cleanedString = response?.replace(nonPrintableRegex, "")
//            val weather = gson.fromJson(cleanedString, Wthr::class.java)
//            val main : View = findViewById(R.id.main)
//            runOnUiThread {
//                if(weather.crnt.temperature_2m < 60){
//                    //turn the background blue
//                    main.setBackgroundColor(Color.BLUE)
//                }else{
//                    //turn the background red
//                    main.setBackgroundColor(Color.RED)
//                }
//            }
//        }

//        val client = OkHttpClient()
//
//        val request = Request.Builder().url("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&with_genres=27&year=2024").get().addHeader("accept", "application/json").addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3Y2U4YzlmMDFlNDNhZDU5NTUzZmNjYmFlZmY4MGJmYyIsIm5iZiI6MTczMzE4MTk4OS4zNzYsInN1YiI6IjY3NGU0MjI1YWE4NDRkYzZlZTk0NDZlZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.CIjbdscpuNHNNHGzT2-eM7JR21RmUgJ_A-V2AgrKdwk").build()
//        val response = client.newCall(request)
//        response.enqueue(callback)
//        val txt1 : TextView = findViewById(R.id.testText)
//        txt1.setText(response.body().toString())
        /*
        layoutManager : LinearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        myList : RecyclerView  = (RecyclerView) findViewById(R.id.my_recycler_view);
        myList.setLayoutManager(layoutManager);
         */
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
