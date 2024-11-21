package com.example.agroeasy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DripPipelinesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drip_pipelines)

        recyclerView = findViewById(R.id.productRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the product list
        productList = ArrayList()

        // Set up the adapter
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Reference to the "products/DripPipelines" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products").child("Drip Pipelines")

        fetchProductsFromFirebase()
    }

    private fun fetchProductsFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear() // Clear the list before adding new data to avoid duplication
                for (productSnapshot in snapshot.children) {
                    val title = productSnapshot.child("title").getValue(String::class.java) ?: "No Title"
                    val description = productSnapshot.child("description").getValue(String::class.java) ?: "No Description"
                    val address = productSnapshot.child("address").getValue(String::class.java) ?: "No Address"
                    val price = productSnapshot.child("price").getValue(Long::class.java) ?: "0"
                    val uploaderName = productSnapshot.child("uploader").child("name").getValue(String::class.java) ?: "Unknown User"
                    val profileImageUrl = productSnapshot.child("uploader").child("profilePhoto").getValue(String::class.java) ?: ""

                    val timestamp = productSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val uploadTime = convertTimestampToDate(timestamp)

                    val productImageUrls = productSnapshot.child("photos").children.mapNotNull { it.getValue(String::class.java) }

                    val product = Product(
                        title = title,
                        description = description,
                        address = address,
                        price = "₹$price",
                        userName = uploaderName,
                        uploadTime = uploadTime,
                        profileImageUrl = profileImageUrl,
                        productImageUrls = productImageUrls,
                        timestamp = timestamp
                    )
                    productList.add(product)
                }

                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DripPipelinesActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("DripPipelinesActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
