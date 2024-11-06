package com.example.agroeasy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PesticidesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops) // Ensure activity_crops layout file exists and includes recyclerView

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty productList and set to RecyclerView
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // Fetch products from Firebase Firestore
        fetchProductData()
    }

    private fun fetchProductData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("products") // Assuming 'products' is your collection name
            .whereEqualTo("category", "Crops") // Filter by category if needed
            .get()
            .addOnSuccessListener { documents ->
                processDocuments(documents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun processDocuments(documents: QuerySnapshot) {
        for (document in documents) {
            val product = document.toObject(Product::class.java)
            productList.add(product)
        }
        productAdapter.notifyDataSetChanged() // Notify adapter of data changes
    }
}
