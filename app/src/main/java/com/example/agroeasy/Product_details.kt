package com.example.agroeasy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ProductDetails : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var uploadPhotoArea: LinearLayout
    private val selectedPhotoUris = mutableListOf<Uri>()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var progressBar: ProgressBar

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedPhotoUris.clear()
        selectedPhotoUris.addAll(uris.take(4)) // Limit to 4 images
        displaySelectedPhotos()
        Log.d("ProductDetails", "Selected images: $selectedPhotoUris")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_details)

        // Initialize UI components
        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        addressInput = findViewById(R.id.addressInput)
        priceInput = findViewById(R.id.priceInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        uploadPhotoArea = findViewById(R.id.uploadPhotoArea)
        progressBar = findViewById(R.id.progressBar)

        setupCategorySpinner()

        uploadPhotoArea.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.sellNowButton).setOnClickListener {
            it.isEnabled = false  // Prevent multiple clicks
            saveProductDetails()
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf(
            "Pesticides", "Crops", "Nursery Plants", "Drip Pipelines",
            "Rentals", "Tractors", "Apiculture", "Poultry"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
    }

    private fun displaySelectedPhotos() {
        uploadPhotoArea.removeAllViews()
        for (uri in selectedPhotoUris) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                    setMargins(8, 8, 8, 8)
                }
                setImageURI(uri)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            uploadPhotoArea.addView(imageView)
        }
    }

    private fun saveProductDetails() {
        progressBar.visibility = View.VISIBLE

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val price = priceInput.text.toString().toDoubleOrNull()
        val category = categorySpinner.selectedItem.toString()
        val uploaderId = FirebaseAuth.getInstance().currentUser?.uid

        if (uploaderId == null || title.isEmpty() || description.isEmpty() || address.isEmpty() || price == null || selectedPhotoUris.isEmpty()) {
            progressBar.visibility = View.GONE
            findViewById<Button>(R.id.sellNowButton).isEnabled = true
            Toast.makeText(this, "Please fill in all fields and select photos", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = database.child("products").child(category).push().key ?: return

        // Fetch uploader details asynchronously
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uploaderName = currentUser?.displayName ?: "Unknown"
        val uploaderEmail = currentUser?.email ?: "Unknown"
        val uploaderProfilePhotoUrl = currentUser?.photoUrl?.toString() ?: ""

        FirebaseDatabase.getInstance().getReference("Users")
            .child(uploaderId).child("mobile")
            .get()
            .addOnSuccessListener { snapshot ->
                val uploaderMobile = snapshot.value?.toString() ?: "N/A"

                val productData = mapOf(
                    "title" to title,
                    "description" to description,
                    "address" to address,
                    "price" to price,
                    "timestamp" to System.currentTimeMillis(), // Timestamp stored as Long
                    "uploaderId" to uploaderId,
                    "uploaderName" to uploaderName,
                    "uploaderEmail" to uploaderEmail,
                    "uploaderMobile" to uploaderMobile,
                    "uploaderProfilePhotoUrl" to uploaderProfilePhotoUrl,
                    "status" to "public",
                    "photos" to emptyList<String>() // Placeholder for photos
                )

                // Save product data
                database.child("products").child(category).child(productId).setValue(productData)
                    .addOnSuccessListener {
                        uploadImagesToFirebase(productId, category)
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.GONE
                        findViewById<Button>(R.id.sellNowButton).isEnabled = true
                        Toast.makeText(this, "Failed to save product details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                findViewById<Button>(R.id.sellNowButton).isEnabled = true
                Toast.makeText(this, "Failed to fetch mobile number: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImagesToFirebase(productId: String, category: String) {
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0
        val totalUploads = selectedPhotoUris.size

        for (uri in selectedPhotoUris) {
            val filePath = "product_images/$productId/${UUID.randomUUID()}"
            val storageRef: StorageReference = storage.reference.child(filePath)

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedUrls.add(downloadUrl.toString())
                        uploadCount++
                        if (uploadCount == totalUploads) {
                            database.child("products").child(category).child(productId)
                                .child("photos").setValue(uploadedUrls)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                                    navigateToCategoryActivity(category)
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    uploadCount++
                    if (uploadCount == totalUploads) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Product uploaded with some images failed", Toast.LENGTH_SHORT).show()
                        navigateToCategoryActivity(category)
                    }
                }
        }
    }

    private fun navigateToCategoryActivity(category: String) {
        val intent = when (category) {
            "Pesticides" -> Intent(this, PesticidesActivity::class.java)
            "Crops" -> Intent(this, CropsActivity::class.java)
            else -> Intent(this, HomePage::class.java)
        }
        startActivity(intent)
        finish()
    }
}
