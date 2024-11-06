package com.example.agroeasy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfilePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private lateinit var etProfileName: EditText
    private lateinit var tvMobileNumber: EditText
    private lateinit var tvUserEmail: EditText
    private lateinit var tvUserAddress: EditText
    private lateinit var profilePicture: ImageView
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var backButton: ImageButton
    private lateinit var tvHelp: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference

        // Initialize UI elements
        etProfileName = findViewById(R.id.profileName)
        tvMobileNumber = findViewById(R.id.tvMobileNumber)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserAddress = findViewById(R.id.tvUserAddress)
        profilePicture = findViewById(R.id.profilePicture)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnLogout = findViewById(R.id.btnLogout)
        backButton = findViewById(R.id.backButton)
        tvHelp = findViewById(R.id.tvHelp)

        // Load user data from Firebase
        loadUserData()

        // Profile picture click listener to choose an image
        profilePicture.setOnClickListener {
            chooseImage()
        }

        // Back button listener
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Update profile button listener
        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        // Logout button listener
        btnLogout.setOnClickListener {
            logoutUser()
        }

        // Need Help button listener
        tvHelp.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // Load profile details from Realtime Database
            databaseRef.child(user.uid).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    etProfileName.setText(dataSnapshot.child("name").value?.toString() ?: "N/A")
                    tvMobileNumber.setText(dataSnapshot.child("mobile").value?.toString() ?: "N/A")
                    tvUserEmail.setText(user.email ?: "N/A")
                    tvUserAddress.setText(dataSnapshot.child("address").value?.toString() ?: "N/A")
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }

            // Load profile picture from Firebase Storage with circular cropping
            val profilePicRef = storageRef.child("users/${user.uid}/profile.jpg")
            profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop() // Use circleCrop() directly
                    .into(profilePicture)
            }.addOnFailureListener {
                profilePicture.setImageResource(R.drawable.img_24) // Set default image if failed
            }
        }
    }

    private fun updateProfile() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userUpdates = mapOf(
                "name" to etProfileName.text.toString(),
                "mobile" to tvMobileNumber.text.toString(),
                "address" to tvUserAddress.text.toString()
            )

            // Updating name, mobile number, and address in the database
            databaseRef.child(user.uid).updateChildren(userUpdates).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }

            // Upload profile picture if changed
            imageUri?.let { uri ->
                uploadProfilePicture(uri, user.uid)
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                profilePicture.setImageBitmap(bitmap) // Set the selected image as bitmap
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadProfilePicture(uri: Uri, uid: String) {
        val profilePicRef = storageRef.child("users/$uid/profile.jpg")
        profilePicRef.putFile(uri).addOnSuccessListener {
            profilePicRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop() // Use circleCrop() directly
                    .into(profilePicture) // Set the uploaded profile picture
                Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showHelpDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Help?")
        builder.setMessage("Name: Abhishek Shelke\nPhone: 7756022207\nEmail: abhishelke42161@gmail.com")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }
}
