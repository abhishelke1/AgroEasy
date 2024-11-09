package com.example.agroeasy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PesticidesActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesticides) // Make sure this XML layout exists

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)

        productList = mutableListOf()
        productAdapter = ProductAdapter(productList)
        productRecyclerView.adapter = productAdapter

        // Firebase Reference to "Pesticides" category in "products" node
        database = FirebaseDatabase.getInstance().reference.child("products").child("Pesticides")

        // Fetch products from Firebase
        fetchProductDetails()
    }

    private fun fetchProductDetails() {
        // Fetch product details from the "Pesticides" node
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PesticidesActivity", "Failed to fetch products: ${error.message}")
            }
        })
    }
}