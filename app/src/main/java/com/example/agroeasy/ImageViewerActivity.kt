// ImageViewerActivity.kt
package com.example.agroeasy

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val imageView: ImageView = findViewById(R.id.imageViewFullScreen)

        // Get the image URL passed from the adapter
        val imageUrl = intent.getStringExtra("imageUrl")

        // Use Glide to load the image into the ImageView
        imageUrl?.let {
            Glide.with(this).load(it).into(imageView)
        }
    }
}
