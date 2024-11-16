package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Scane : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scane)

        // Back Button Navigation
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Navigate back to HomePage activity
            navigateToHomePage()
        }

        // Upload Crop Button Click Listener (This should be an ImageButton, not LinearLayout)
        val uploadCropButton: ImageButton = findViewById(R.id.uploadCropButton)
        uploadCropButton.setOnClickListener {
            // Show a toast message indicating under construction
            showUnderConstructionMessage()
        }

        // Set up the bottom navigation buttons
        setupBottomNavigation()
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomePage::class.java)
        startActivity(intent)
        finish() // Finish QRActivity to prevent back navigation loop
    }

    private fun showUnderConstructionMessage() {
        Toast.makeText(
            this,
            "This feature is under construction. We are working on it and will update soon.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setupBottomNavigation() {
        // Home Icon
        val homeIcon: ImageButton = findViewById(R.id.homeIcon)
        homeIcon.setOnClickListener {
            // Navigate to HomePage
            navigateToHomePage()
        }

        // Video Icon
        val videoIcon: ImageButton = findViewById(R.id.videoIcon)
        videoIcon.setOnClickListener {
            // Navigate to ReelActivity
            val intent = Intent(this, ReelActivity::class.java)
            startActivity(intent)
        }

        // Account Icon
        val accountIcon: ImageButton = findViewById(R.id.accountIcon)
        accountIcon.setOnClickListener {
            // Navigate to SellProductActivity
            val intent = Intent(this, SellProductActivity::class.java)
            startActivity(intent)
        }
    }
}
