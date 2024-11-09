package com.example.agroeasy

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.ValueEventListener

class SlideMenuActivity : AppCompatActivity() {

    private lateinit var profileIcon: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_menu)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance()

        // Initialize UI elements
        initializeUIElements()

        // Load user profile data
        loadUserProfile()

        val yourProductsLayout: LinearLayout = findViewById(R.id.yourProductsLayout)
        yourProductsLayout.setOnClickListener {
            val intent = Intent(this, MyProductActivity::class.java)  // Replace with your target activity
            startActivity(intent)
        }


        // Set OnClickListener for each menu item
        findViewById<LinearLayout>(R.id.layoutGovernmentPolicies).setOnClickListener {
            showPopup("Government Policies (Agriculture-Related Policies)", getGovernmentPoliciesContent())
        }
        findViewById<LinearLayout>(R.id.layoutTermsAndConditions).setOnClickListener {
            showPopup("Terms and Conditions", getTermsContent())
        }
        findViewById<LinearLayout>(R.id.layoutKnowAboutUs).setOnClickListener {
            showPopup("Know About Us (About AgroEasy)", getKnowAboutUsContent())
        }
        // WhatsApp functionality
        findViewById<LinearLayout>(R.id.layoutWhatsApp).setOnClickListener {
            openWhatsAppChannel()
        }

        // YouTube functionality
        findViewById<LinearLayout>(R.id.layoutYouTube).setOnClickListener {
            openYouTubeChannel()
        }
        findViewById<LinearLayout>(R.id.layoutfacebook).setOnClickListener {
            openfacebook()
        }

        // Logout functionality
        findViewById<LinearLayout>(R.id.layoutLogout).setOnClickListener {
            logoutUser()
        }

        // Contact Us functionality
        findViewById<LinearLayout>(R.id.layoutContactUs).setOnClickListener {
            showContactDialog()
        }

        // Set an OnClickListener to redirect to ProfilePageActivity when clicked
        findViewById<TextView>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(this, ProfilePageActivity::class.java))
        }
    }

    private fun initializeUIElements() {
        profileIcon = findViewById(R.id.ivProfilePicture)
        userNameTextView = findViewById(R.id.tvUserName)
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            databaseRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").getValue(String::class.java)
                        userNameTextView.text = userName ?: "User Name"
                        loadProfilePicture(currentUser.uid)
                    } else {
                        showToast("User data not found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Database error: ${error.message}")
                }
            })
        } else {
            showToast("User not authenticated.")
        }
    }

    private fun loadProfilePicture(uid: String) {
        val profilePicRef = storageRef.getReference("users/$uid/profile.jpg")
        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(profileIcon)
        }.addOnFailureListener {
            profileIcon.setImageResource(R.drawable.img_24) // Fallback image
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPopup(title: String, content: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(content)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
        builder.create().show()
    }

    private fun getGovernmentPoliciesContent(): String {
        return """
            The agricultural sector is vital to the economy, and the government provides various policies to support farmers and promote sustainable practices. Some of the key policies include:
            
            Pradhan Mantri Fasal Bima Yojana (PMFBY):
            This scheme provides crop insurance to farmers, covering losses due to natural disasters like floods, droughts, and storms. The government aims to ensure financial stability for farmers during tough times.

            Soil Health Card Scheme:
            Under this initiative, farmers receive soil health cards, which provide information about the nutrients present in their soil and recommendations on how to improve soil quality for better crop yield.

            Kisan Credit Card (KCC):
            The KCC scheme offers short-term credit to farmers for purchasing seeds, fertilizers, pesticides, and other agricultural inputs. The aim is to provide financial assistance for timely agricultural operations.

            Minimum Support Price (MSP):
            The government sets a minimum support price for various crops to protect farmers from price fluctuations. Farmers are guaranteed a fair price for their produce under this policy.

            National Agriculture Market (e-NAM):
            e-NAM is a pan-India electronic trading platform for agricultural commodities. It facilitates better price discovery through transparent online auctions, providing farmers with better access to markets.

            These policies are designed to boost agricultural productivity, ensure fair pricing, and support farmers in achieving sustainable livelihoods. AgroEasy provides regular updates on these policies and how they can benefit you.
        """.trimIndent()
    }

    private fun getTermsContent(): String {
        return """
            Welcome to AgroEasy! By downloading or using the app, you agree to the following terms and conditions:

            User Responsibilities:
            Users must provide accurate and complete information during registration. Users are responsible for maintaining the confidentiality of their login credentials.

            Use of Services:
            The services provided by AgroEasy are for informational and practical purposes only. Users must comply with all applicable laws and regulations when using the app.

            Content Ownership:
            All content, including text, images, and data within AgroEasy, is the intellectual property of AgroEasy unless stated otherwise. Users must not copy, reproduce, or distribute any content without permission.

            Privacy:
            We respect your privacy. Your personal data, including your name, contact information, and agricultural preferences, will only be used to enhance your experience within the app and will not be shared with third parties without your consent. For more details, refer to our Privacy Policy.

            Limitation of Liability:
            AgroEasy shall not be held liable for any loss or damage resulting from the use of the app. Users acknowledge that agricultural outcomes depend on a variety of factors outside the app’s control.

            Changes to Terms:
            We may update these terms from time to time. Continued use of the app signifies your acceptance of the updated terms.

            If you have any questions, feel free to contact our support team.
        """.trimIndent()
    }

    private fun getKnowAboutUsContent(): String {
        return """
            AgroEasy is a user-friendly platform designed to empower farmers and agricultural enthusiasts by providing easy access to essential tools, resources, and information. Our app offers a wide range of features to assist farmers in improving productivity, making informed decisions, and connecting with markets.

            Our Mission:
            To support sustainable agricultural growth by making advanced farming knowledge, technology, and resources available at the fingertips of every farmer.

            What We Offer:
            - Access to high-quality seeds, fertilizers, and pesticides.
            - Expert guidance on crop management and soil health.
            - Updates on weather conditions, market prices, and government policies.
            - A marketplace to buy and sell agricultural products.

            At AgroEasy, we strive to create a community where farmers can learn, grow, and thrive together. We are committed to bridging the gap between technology and traditional farming practices.
        """.trimIndent()
    }

    // Logout functionality
    private fun logoutUser() {
        auth.signOut() // Sign out the user
        // Redirect to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish this activity if you don't want to go back
    }

    // Contact Us dialog
    private fun showContactDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Contact Us")
        builder.setMessage("Name: Abhishek Shelke\nPhone: 7756022207\nEmail: abhishelke42161@gmail.com")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    // Open WhatsApp Channel
    private fun openWhatsAppChannel() {
        val url = "https://whatsapp.com/channel/0029VavOYbG9MF96jBUzul1C"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    // Open YouTube Channel
    private fun openYouTubeChannel() {
        val url = "https://www.youtube.com/@agroeasy"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
    private fun openfacebook() {
        val url = "https://www.facebook.com/share/BuwudsQya1dVBjdV/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
