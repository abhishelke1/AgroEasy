package com.example.agroeasy

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ResultActivity : AppCompatActivity() {

    private lateinit var imageUrl: String
    private lateinit var resultText: TextView
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultText = findViewById(R.id.result_text)
        storageReference = FirebaseStorage.getInstance().reference

        // Get the image URL passed from CameraActivity
        imageUrl = intent.getStringExtra("image_url") ?: ""

        // Here, you'll need to process the image and use Firebase ML Kit to detect plant disease
        processImageForDisease(imageUrl)
    }

    private fun processImageForDisease(imageUrl: String) {
        // Firebase ML Kit inference logic goes here
        // For example, load the model, pass the image, and get predictions
        // Update resultText based on disease and health tips
    }
}
