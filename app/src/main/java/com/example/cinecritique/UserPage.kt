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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

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

        val button1 = findViewById<MaterialButton>(R.id.ratingButton1)
        val button2 = findViewById<MaterialButton>(R.id.ratingButton2)
        val button3 = findViewById<MaterialButton>(R.id.ratingButton3)
        val button4 = findViewById<MaterialButton>(R.id.ratingButton4)
        val button5 = findViewById<MaterialButton>(R.id.ratingButton5)

        // Initialize the rating bar functionality
        showRatingBar(button1, button2, button3, button4, button5)

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