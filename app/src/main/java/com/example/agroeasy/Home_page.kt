package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class HomePage : AppCompatActivity() {

    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var videoIcon: ImageButton // Declare the video icon
    private lateinit var accountIcon: ImageButton // Declare the account icon
    private lateinit var btnPesticides: ImageButton // Declare the product buttons
    private lateinit var btnCrops: ImageButton
    private lateinit var btnNurseryPlants: ImageButton
    private lateinit var btnDripPipelines: ImageButton
    private lateinit var btnPoultry: ImageButton
    private lateinit var btnApiculture: ImageButton
    private lateinit var btnTractors: ImageButton
    private lateinit var btnRentals: ImageButton

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
        profileIcon = findViewById(R.id.profileIcon)
        menuIcon = findViewById(R.id.menuIcon)
        videoIcon = findViewById(R.id.videoIcon) // Initialize the video icon
        accountIcon = findViewById(R.id.accountIcon) // Initialize the account icon
        btnPesticides = findViewById(R.id.btnPesticides) // Initialize product category buttons
        btnCrops = findViewById(R.id.btnCrops)
        btnNurseryPlants = findViewById(R.id.btnNurseryPlants)
        btnDripPipelines = findViewById(R.id.btnDripPipelines)
        btnPoultry = findViewById(R.id.btnPoultry)
        btnApiculture = findViewById(R.id.btnApiculture)
        btnTractors = findViewById(R.id.btnTractors)
        btnRentals = findViewById(R.id.btnRentals)

        // Load user profile picture
        loadProfilePicture()

        // Set an OnClickListener to redirect to ProfilePageActivity when clicked
        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
        }

        // Set an OnClickListener for the menu icon to open the SlideMenuActivity
        menuIcon.setOnClickListener {
            val intent = Intent(this, SlideMenuActivity::class.java)
            startActivity(intent)
        }

        // Set an OnClickListener for the reel icon to open the ReelActivity
        videoIcon.setOnClickListener {
            val intent = Intent(this, ReelActivity::class.java)
            startActivity(intent) // Redirect to the ReelActivity
        }

        // Set an OnClickListener for the account icon to open the SellProductActivity
        accountIcon.setOnClickListener {
            val intent = Intent(this, SellProductActivity::class.java)
            startActivity(intent) // Redirect to SellProductActivity
        }

        // Set OnClickListeners for the product category buttons
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
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val profilePicRef = storageRef.getReference("users/${user.uid}/profile.jpg")
            profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(profileIcon)
            }.addOnFailureListener {
                profileIcon.setImageResource(R.drawable.img_24) // Default image
            }
        }
    }
}
