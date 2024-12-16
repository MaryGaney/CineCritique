package com.example.cinecritique

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GenrePick : AppCompatActivity() {

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
        pfp.setOnClickListener {
            val intent = Intent(this, UserPage::class.java)
            startActivity(intent)
        }

        //logo takes you to main page
        val mainpagebtn: ImageView = findViewById(R.id.mainpagebtn)
        mainpagebtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val popularButton : Button = findViewById(R.id.popularButton)
        popularButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Popular")
            }
            startActivity(intent)
        }
        val actionButton : Button = findViewById(R.id.actionButton)
        actionButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Action")
            }
            startActivity(intent)
        }
        val adventureButton : Button = findViewById(R.id.adventureButton)
        adventureButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Adventure")
            }
            startActivity(intent)
        }
        val animationButton : Button = findViewById(R.id.animationButton)
        animationButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Animation")
            }
            startActivity(intent)
        }
        val comedyButton : Button = findViewById(R.id.comedyButton)
        comedyButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Comedy")
            }
            startActivity(intent)
        }
        val documentaryButton : Button = findViewById(R.id.documentaryButton)
        documentaryButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
               putExtra("GENRE_NAME", "Documentary")
            }
            startActivity(intent)
        }
        val dramaButton : Button = findViewById(R.id.dramaButton)
        dramaButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Drama")
            }
            startActivity(intent)
        }
        val familyButton : Button = findViewById(R.id.familyButton)
        familyButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Family")
            }
            startActivity(intent)
        }
        val fantasyButton : Button = findViewById(R.id.fantasyButton)
        fantasyButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Fantasy")
            }
            startActivity(intent)
        }
        val historyButton : Button = findViewById(R.id.historyButton)
        historyButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "History")
            }
            startActivity(intent)
        }
        val horrorButton : Button = findViewById(R.id.horrorButton)
        horrorButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Horror")
            }
            startActivity(intent)
        }
        val musicButton : Button = findViewById(R.id.musicButton)
        musicButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Music")
            }
            startActivity(intent)
        }
        val mysteryButton : Button = findViewById(R.id.mysteryButton)
        mysteryButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Mystery")
            }
            startActivity(intent)
        }
        val romanceButton : Button = findViewById(R.id.romanceButton)
        romanceButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Romance")
            }
            startActivity(intent)
        }
        val scienceButton : Button = findViewById(R.id.scienceButton)
        scienceButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Science Fiction")
            }
            startActivity(intent)
        }
        val tvButton : Button = findViewById(R.id.tvButton)
        tvButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "TV Movie")
            }
            startActivity(intent)
        }
        val thrillerButton : Button = findViewById(R.id.thrillerButton)
        thrillerButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Thriller")
            }
            startActivity(intent)
        }
        val warButton : Button = findViewById(R.id.warButton)
        warButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "War")
            }
            startActivity(intent)
        }
        val westernButton : Button = findViewById(R.id.westernButton)
        westernButton.setOnClickListener {
            val intent = Intent(this, MovieGenre::class.java).apply{
                putExtra("GENRE_NAME", "Western")
            }
            startActivity(intent)
        }

    }
}