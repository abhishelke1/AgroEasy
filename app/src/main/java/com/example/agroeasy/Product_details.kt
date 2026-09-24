package com.example.agroeasy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
    private lateinit var photoPlaceholder: TextView
    private lateinit var progressBar: ProgressBar
    private val selectedPhotoUris = mutableListOf<Uri>()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.size > 4) {
                Toast.makeText(this, "You can upload a maximum of 4 photos", Toast.LENGTH_SHORT).show()
            }
            selectedPhotoUris.clear()
            selectedPhotoUris.addAll(uris.take(4)) // Limit to 4 images
            displaySelectedPhotos()
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
        photoPlaceholder = findViewById(R.id.photoPlaceholder)
        progressBar = findViewById(R.id.progressBar)

        setupCategorySpinner()

        uploadPhotoArea.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.sellNowButton).setOnClickListener {
            it.isEnabled = false // Prevent multiple clicks
            if (validateInputs()) {
                saveProductDetails()
            } else {
                it.isEnabled = true
            }
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.product_categories)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
        categorySpinner.setSelection(0) // Default selection
    }

    private fun displaySelectedPhotos() {
        uploadPhotoArea.removeAllViews()
        if (selectedPhotoUris.isEmpty()) {
            photoPlaceholder.visibility = View.VISIBLE
        } else {
            photoPlaceholder.visibility = View.GONE
            for (uri in selectedPhotoUris) {
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                        setMargins(8, 8, 8, 8)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                Glide.with(this).load(uri).into(imageView)
                uploadPhotoArea.addView(imageView)
            }
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            titleInput.text.toString().trim().isEmpty() -> {
                titleInput.error = "Title is required"
                false
            }
            descriptionInput.text.toString().trim().isEmpty() -> {
                descriptionInput.error = "Description is required"
                false
            }
            priceInput.text.toString().toDoubleOrNull() == null -> {
                priceInput.error = "Valid price is required"
                false
            }
            categorySpinner.selectedItem.toString() == "Select Category" -> {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                false
            }
            selectedPhotoUris.isEmpty() -> {
                Toast.makeText(this, "Please upload at least one photo", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun saveProductDetails() {
        setLoading(true)

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val price = priceInput.text.toString().toDoubleOrNull()
        val category = categorySpinner.selectedItem.toString()
        val uploaderId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val productId = database.child("products").child(category).push().key ?: return

        val productData = mapOf(
            "title" to title,
            "description" to description,
            "address" to address,
            "price" to price,
            "timestamp" to System.currentTimeMillis(),
            "uploaderId" to uploaderId,
            "status" to "public",
            "photos" to emptyList<String>()
        )

        database.child("products").child(category).child(productId).setValue(productData)
            .addOnSuccessListener {
                uploadImagesToFirebase(productId, category)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Failed to save product details: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                    setLoading(false)
                                    Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                                    navigateToCategoryActivity(category)
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    uploadCount++
                    if (uploadCount == totalUploads) {
                        setLoading(false)
                        Toast.makeText(this, "Product uploaded with some images failed", Toast.LENGTH_SHORT).show()
                        navigateToCategoryActivity(category)
                    }
                }
        }
    }

    private fun navigateToCategoryActivity(category: String) {
        val intent = Intent(this, HomePage::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.sellNowButton).isEnabled = !isLoading
    }
}
