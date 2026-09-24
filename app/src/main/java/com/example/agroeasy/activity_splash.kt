package com.example.agroeasy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AgroEasyPrefs", MODE_PRIVATE)

        // Check if the user is already logged in
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                // User is already logged in, go to HomePage
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, go to WelcomeActivity
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000) // 2 seconds delay
    }
}
