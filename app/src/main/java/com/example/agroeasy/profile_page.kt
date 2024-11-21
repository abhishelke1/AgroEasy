package com.example.agroeasy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import android.view.View

class ProfilePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private lateinit var etProfileName: EditText
    private lateinit var tvMobileNumber: EditText
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserAddress: EditText
    private lateinit var profilePicture: CircleImageView
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var tvHelp: TextView
    private lateinit var btnAddMarketRate: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference

        etProfileName = findViewById(R.id.profileName)
        tvMobileNumber = findViewById(R.id.tvMobileNumber)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserAddress = findViewById(R.id.tvUserAddress)
        profilePicture = findViewById(R.id.profilePicture)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnLogout = findViewById(R.id.btnLogout)
        tvHelp = findViewById(R.id.tvHelp)
        btnAddMarketRate = findViewById(R.id.btnAddMarketRate)

        loadUserProfile()

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnAddMarketRate.setOnClickListener {
            showPinDialog()
        }

        tvHelp.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Need Help?")
                .setMessage("Contact Name: Abhishek Shelke\nPhone: 7756022207\nEmail: abhishelke42161@gmail.com")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        // Profile Picture click listener
        profilePicture.setOnClickListener {
            openImageChooser()
        }
    }

    private fun loadUserProfile() {
        // Show the progress bar while loading
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val user = auth.currentUser
        user?.let {
            tvUserEmail.text = it.email
            databaseRef.child(it.uid).get().addOnSuccessListener { snapshot ->
                // Hide the progress bar once the data is loaded
                progressBar.visibility = View.GONE

                if (snapshot.exists()) {
                    etProfileName.setText(snapshot.child("name").value.toString())
                    tvMobileNumber.setText(snapshot.child("mobile").value.toString())
                    tvUserAddress.setText(snapshot.child("address").value?.toString() ?: "")

                    // Load profile picture if exists
                    val profileImageUrl = snapshot.child("profilePicture").value.toString()
                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .circleCrop()  // Apply circular crop transformation
                            .into(profilePicture)
                    }
                }
            }.addOnFailureListener {
                // Hide the progress bar in case of failure as well
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImageChooser() {
        // Intent to pick an image from gallery
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            // Display the selected image in the CircleImageView
            profilePicture.setImageURI(imageUri)
        }
    }

    private fun updateProfile() {
        val updates = mapOf(
            "name" to etProfileName.text.toString(),
            "mobile" to tvMobileNumber.text.toString(),
            "address" to tvUserAddress.text.toString()
        )

        auth.currentUser?.let {
            if (imageUri != null) {
                // Upload profile picture to Firebase Storage
                val profilePicRef = storageRef.child("profile_pictures/${it.uid}.jpg")
                profilePicRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                    // Get the URL of the uploaded profile picture
                    profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                        val profilePicUrl = uri.toString()
                        // Add profile picture URL to the updates map
                        val updatedData = updates + ("profilePicture" to profilePicUrl)

                        // Update the user's data in Firebase Database
                        databaseRef.child(it.uid).updateChildren(updatedData).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to upload profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update without changing the profile picture
                databaseRef.child(it.uid).updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showPinDialog() {
        val pinDialog = AlertDialog.Builder(this)
        val pinInput = EditText(this)
        pinInput.hint = "Enter Admin PIN"
        pinInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD

        pinDialog.setTitle("Admin Authentication")
            .setView(pinInput)
            .setPositiveButton("Submit") { dialog, _ ->
                val enteredPin = pinInput.text.toString()
                val correctPin = "2000"

                if (enteredPin == correctPin) {
                    val intent = Intent(this, MarketplaceActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Wrong PIN. Access denied.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

