package com.example.agroeasy

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the selected language from the intent
        val language = intent.getStringExtra("LANGUAGE") ?: "en"

        // Set the selected language before setting content view
        setLocale(language)
        setContentView(R.layout.activity_main)

        // Display the selected language on a TextView (optional)
        val languageTextView: TextView = findViewById(R.id.languageTextView)
        languageTextView.text = "Selected Language: $language"
    }

    // Function to update the app's locale based on the selected language
    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
