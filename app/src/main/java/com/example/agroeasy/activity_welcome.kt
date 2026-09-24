package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)



        val getStartedButton: Button = findViewById(R.id.btnGetStarted)


        getStartedButton.setOnClickListener {
            // Navigate to the LanguageSelectionActivity
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}
