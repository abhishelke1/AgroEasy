package com.example.agroeasy

import android.app.Activity
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
        selectedPhotoUris.addAll(uris.take(4))
        displaySelectedPhotos()
        Log.d("ProductDetails", "Selected images: $selectedPhotoUris") // Logging selected images
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        setContentView(R.layout.activity_product_details)

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
            saveProductDetails()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailsContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val price = priceInput.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()

        if (title.isEmpty() || description.isEmpty() || address.isEmpty() || price.isEmpty() || selectedPhotoUris.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and select photos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!price.matches(Regex("^[0-9]+(\\.[0-9]{1,2})?$"))) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = database.child("products").push().key // Generate a unique key for the product
        val productData = hashMapOf(
            "title" to title,
            "description" to description,
            "address" to address,
            "price" to price.toDouble(),
            "category" to category,
            "timestamp" to System.currentTimeMillis()
        )

        progressBar.visibility = View.VISIBLE
        Log.d("ProductDetails", "Saving product details: $productData")

        // Save product data to Realtime Database
        if (productId != null) { // Ensure productId is not null
            database.child("products").child(productId).setValue(productData)
                .addOnSuccessListener {
                    uploadImagesToFirebase(productId, category)  // Use productId to upload images
                    Log.d("ProductDetails", "Product details saved with ID: $productId")
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to save product details: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProductDetails", "Failed to save product details: ${e.message}", e)
                }
        }
    }

    private fun uploadImagesToFirebase(productId: String, category: String) {
        if (selectedPhotoUris.isEmpty()) {
            // No images to upload, directly complete the upload process
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Product uploaded successfully without images", Toast.LENGTH_SHORT).show()
            navigateToCategoryActivity(category)
            return
        }

        var uploadCount = 0
        val totalUploads = selectedPhotoUris.size

        for (uri in selectedPhotoUris) {
            val filePath = "product_images/$productId/${UUID.randomUUID()}"
            val storageRef: StorageReference = storage.reference.child(filePath)

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        database.child("products").child(productId)
                            .child("photoUrls").child(UUID.randomUUID().toString()).setValue(downloadUrl.toString())
                            .addOnSuccessListener {
                                Log.d("ProductDetails", "Image URL added to Realtime Database: $downloadUrl")
                                uploadCount++ // Increment the successful upload count

                                // Check if all images have been uploaded
                                if (uploadCount == totalUploads) {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                                    navigateToCategoryActivity(category)  // Navigate to the respective activity based on category
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ProductDetails", "Failed to add image URL: ${e.message}", e)
                                // Count this upload to complete the flow
                                uploadCount++
                                // Check if all images have been processed
                                if (uploadCount == totalUploads) {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Product uploaded with some images failed", Toast.LENGTH_SHORT).show()
                                    navigateToCategoryActivity(category)
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProductDetails", "Failed to upload image: ${e.message}", e)
                    // Count this upload to complete the flow
                    uploadCount++
                    // Check if all images have been processed
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
            "Nursery Plants" -> Intent(this, NurseryPlantsActivity::class.java)
            "Drip Pipelines" -> Intent(this, DripPipelinesActivity::class.java)
            "Rentals" -> Intent(this, RentalsActivity::class.java)
            "Tractors" -> Intent(this, TractorsActivity::class.java)
            "Apiculture" -> Intent(this, ApicultureActivity::class.java)
            "Poultry" -> Intent(this, PoultryActivity::class.java)
            else -> Intent(this, BuySection::class.java)  // Fallback to BuySection if no match
        }
        startActivity(intent)
        finish()
    }
}
