package com.example.agroeasy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CropsActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops)  // Refers to the layout file activity_crops.xml

        // Initialize RecyclerView and Adapter
        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)




        // Firebase Reference to "Crops" category in "products" node
        database = FirebaseDatabase.getInstance().reference.child("products").child("Crops")

        // Fetch products from Firebase
        fetchProductDetails()
    }

    private fun fetchProductDetails() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

                // Check if data exists in Firebase
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let {
                            productList.add(it)
                        }
                    }

                } else {
                    // Handle empty state when no products are available
                    Log.d("CropsActivity", "No products available.")
                    Toast.makeText(this@CropsActivity, "No products available in Crops category.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log and show error message if fetching data fails
                Log.e("CropsActivity", "Failed to fetch products: ${error.message}")
                Toast.makeText(this@CropsActivity, "Failed to load products. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
