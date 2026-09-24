
package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        // Define UI elements
        val englishOption: RadioButton = findViewById(R.id.radioEnglish)
        val hindiOption: RadioButton = findViewById(R.id.radioHindi)
        val nextButton: Button = findViewById(R.id.btnNext)

        // Back Button
        val backButton: ImageView = findViewById(R.id.backButton)

        // Set OnClickListener for the Next button
        nextButton.setOnClickListener {
            val selectedLanguage = when {
                englishOption.isChecked -> "English"
                hindiOption.isChecked -> "हिंदी"  // For Marathi language when Hindi is selected
                else -> "English"
            }

            // Pass the selected language to the next activity
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("LANGUAGE", selectedLanguage) // Pass the selected language
            startActivity(intent)
        }

        // Set OnClickListener for the Back button to navigate to the previous activity
        backButton.setOnClickListener {
            onBackPressed() // This will navigate back to the previous screen (WelcomeActivity)
        }
    }
}
