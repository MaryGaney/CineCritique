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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import okhttp3.Request
import org.chromium.net.CronetEngine
import java.util.concurrent.Executor
import java.util.concurrent.Executors



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

        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        signInLauncher.launch(signInIntent)

        //profile pic image takes you to user page
        val pfp: ImageView = findViewById(R.id.pfp)
        //val pf: ImageView = findViewById(R.id.imageView)
        pfp.setOnClickListener {
            val intent = Intent(this, UserPage::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
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

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK){
            //successful sign in
            val user = FirebaseAuth.getInstance().currentUser
            user?.let{
                //do something here
                Log.i("MYTAG","signed in")
            }
        } else{
            Log.i("MYTAG","failed sign in")
        }
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
