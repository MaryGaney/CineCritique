package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GenrePick : AppCompatActivity() {

    companion object {
        var userId: String? = null
        private const val ADD_TASK_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_genre_pick)
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
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }
    }
}