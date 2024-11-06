package com.example.agroeasy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BuySection : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_section)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        loadProducts()
    }

    private fun loadProducts() {
        db.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by newest first
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    // Safely retrieve values with default values
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val uploaderName = document.getString("uploaderName") ?: "Unknown"
                    val uploaderProfileImage = document.getString("uploaderProfileImage") ?: ""
                    val postTime = document.getString("postTime") ?: ""
                    val images = document.get("photoUrls") as? List<String> ?: emptyList()

                    // Check if title is not empty before creating a Product instance
                    if (title.isNotEmpty()) {
                        val product = Product(
                            title = title,
                            description = description,
                            uploaderName = uploaderName,
                            uploaderProfileImage = uploaderProfileImage,
                            postTime = postTime,
                            images = images
                        )
                        productList.add(product)
                    } else {
                        Toast.makeText(this, "Product title is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
