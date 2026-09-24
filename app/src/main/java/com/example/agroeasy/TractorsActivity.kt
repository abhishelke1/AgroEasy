package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TractorsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var btnMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tractors)

        recyclerView = findViewById(R.id.productRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the product list
        productList = ArrayList()

        // Set up the adapter
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Reference to the "products/Tractors" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products").child("Tractors")

        fetchProductsFromFirebase()
        btnMenu = findViewById(R.id.btnMenu) // Ensure btnMenu exists as ImageButton in your layout XML
        btnMenu.setOnClickListener {
            // Redirect to SlideMenuActivity
            startActivity(Intent(this, SlideMenuActivity::class.java))
        }
    }

    private fun fetchProductsFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear() // Clear the list before adding new data to avoid duplication
                for (productSnapshot in snapshot.children) {
                    // Read each product and map to Product class
                    val title = productSnapshot.child("title").getValue(String::class.java) ?: "No Title"
                    val description = productSnapshot.child("description").getValue(String::class.java) ?: "No Description"
                    val address = productSnapshot.child("address").getValue(String::class.java) ?: "No Address"
                    val price = productSnapshot.child("price").getValue(Long::class.java) ?: 0L
                    val uploaderName = productSnapshot.child("uploader").child("name").getValue(String::class.java) ?: "AgroEasy"
                    var profileImageUrl = productSnapshot.child("uploader").child("profilePhoto").getValue(String::class.java)

                    // Use default image if no URL or empty string
                    if (profileImageUrl.isNullOrEmpty()) {
                        profileImageUrl = "android.resource://${packageName}/drawable/ic_logo"
                    }

                    // Handle timestamp properly as a Long value
                    val timestamp = productSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val uploadTime = convertTimestampToDate(timestamp)

                    val productImageUrls = productSnapshot.child("photos").children.mapNotNull { it.getValue(String::class.java) }

                    // Create Product object and add it to the list
                    val product = Product(
                        title = title,
                        description = description,
                        address = address,
                        price = "₹$price",
                        userName = uploaderName,
                        uploadTime = uploadTime,
                        profileImageUrl = profileImageUrl,
                        productImageUrls = productImageUrls,
                        timestamp = timestamp // Store timestamp as Long
                    )
                    productList.add(product)
                }

                productAdapter.notifyDataSetChanged() // Notify the adapter about data changes
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TractorsActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("TractorsActivity", "Database error: ${error.message}")
            }
        })
    }

    // Convert Long timestamp to formatted date string
    private fun convertTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) // Change format as needed
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
