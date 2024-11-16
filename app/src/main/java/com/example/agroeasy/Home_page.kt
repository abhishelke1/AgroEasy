package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.view.View


class HomePage : AppCompatActivity() {

    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var videoIcon: ImageButton
    private lateinit var accountIcon: ImageButton
    private lateinit var btnPesticides: ImageButton
    private lateinit var btnCrops: ImageButton
    private lateinit var btnNurseryPlants: ImageButton
    private lateinit var btnDripPipelines: ImageButton
    private lateinit var btnPoultry: ImageButton
    private lateinit var btnApiculture: ImageButton
    private lateinit var btnTractors: ImageButton
    private lateinit var btnRentals: ImageButton
    private lateinit var qrIcon: ImageButton
    private lateinit var progressBar: ProgressBar


    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance()

        // Initialize UI elements
        menuIcon = findViewById(R.id.menuIcon)
        profileIcon = findViewById(R.id.profileIcon)
        menuIcon = findViewById(R.id.menuIcon)
        videoIcon = findViewById(R.id.videoIcon)
        accountIcon = findViewById(R.id.accountIcon)
        btnPesticides = findViewById(R.id.btnPesticides)
        btnCrops = findViewById(R.id.btnCrops)
        btnNurseryPlants = findViewById(R.id.btnNurseryPlants)
        btnDripPipelines = findViewById(R.id.btnDripPipelines)
        btnPoultry = findViewById(R.id.btnPoultry)
        btnApiculture = findViewById(R.id.btnApiculture)
        btnTractors = findViewById(R.id.btnTractors)
        btnRentals = findViewById(R.id.btnRentals)
        qrIcon = findViewById(R.id.qrIcon)
        progressBar = findViewById(R.id.progressBar)


        // Load user profile picture
        loadProfilePicture()

        // Set OnClickListener for the profile icon
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
        }
        menuIcon.setOnClickListener {
            val intent = Intent(this, SlideMenuActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for other buttons as per your previous code...
        videoIcon.setOnClickListener {
            val intent = Intent(this, ReelActivity::class.java)
            startActivity(intent) // Redirect to the ReelActivity
        }

        accountIcon.setOnClickListener {
            val intent = Intent(this, SellProductActivity::class.java)
            startActivity(intent) // Redirect to SellProductActivity
        }

        qrIcon.setOnClickListener {
            val intent = Intent(this, Scane::class.java)
            startActivity(intent) // Redirect to SellProductActivity
        }

        btnPesticides.setOnClickListener {
            val intent = Intent(this, PesticidesActivity::class.java)
            startActivity(intent)
        }

        btnCrops.setOnClickListener {
            val intent = Intent(this, CropsActivity::class.java)
            startActivity(intent)
        }

        btnNurseryPlants.setOnClickListener {
            val intent = Intent(this, NurseryPlantsActivity::class.java)
            startActivity(intent)
        }

        btnDripPipelines.setOnClickListener {
            val intent = Intent(this, DripPipelinesActivity::class.java)
            startActivity(intent)
        }

        btnPoultry.setOnClickListener {
            val intent = Intent(this, PoultryActivity::class.java)
            startActivity(intent)
        }

        btnApiculture.setOnClickListener {
            val intent = Intent(this, ApicultureActivity::class.java)
            startActivity(intent)
        }

        btnTractors.setOnClickListener {
            val intent = Intent(this, TractorsActivity::class.java)
            startActivity(intent)
        }

        btnRentals.setOnClickListener {
            val intent = Intent(this, RentalsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProfilePicture() {
        val user = auth.currentUser
        user?.let {
            // Show the progress bar while loading the profile
            progressBar.visibility = View.VISIBLE

            // Fetch user data from Firebase Database
            databaseRef.child(it.uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Get the profile picture URL from Firebase
                    val profilePicUrl = snapshot.child("profilePicture").value.toString()

                    // If a profile picture URL exists, load it using Glide
                    if (profilePicUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profilePicUrl)
                            .into(profileIcon) // Display the profile picture in profileIcon ImageView
                    } else {
                        // Optionally set a placeholder or default image if there's no URL
                        profileIcon.setImageResource(R.drawable.img_24)
                    }
                }
                // Hide progress bar after loading the profile
                progressBar.visibility = View.GONE
            }.addOnFailureListener {
                // Handle failure
                progressBar.visibility = View.GONE
                // Optionally, show a toast message
            }
        }
    }
}
