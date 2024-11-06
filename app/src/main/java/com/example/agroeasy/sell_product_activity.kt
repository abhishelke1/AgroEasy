package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class SellProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sell_product)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_navigation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back Button
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Navigate back to HomePage Activity
            finish() // Go back to the previous activity
        }

        // Home Icon
        val homeIcon: ImageButton = findViewById(R.id.homeIcon)
        homeIcon.setOnClickListener {
            // Navigate to HomePage Activity
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish() // Optional: call finish if you don't want to keep this activity in the back stack
        }

        // Video Icon
        val videoIcon: ImageButton = findViewById(R.id.videoIcon)
        videoIcon.setOnClickListener {
            // Navigate to ReelActivity
            val intent = Intent(this, ReelActivity::class.java)
            startActivity(intent)
            finish() // Optional: call finish if you don't want to keep this activity in the back stack
        }

        // Sell Now Button
        val sellNowButton: Button = findViewById(R.id.sellNowButton)
        sellNowButton.setOnClickListener {
            // Open ProductInfo Activity
            val intent = Intent(this,ProductDetails::class.java)
            startActivity(intent)
        }
    }
}
