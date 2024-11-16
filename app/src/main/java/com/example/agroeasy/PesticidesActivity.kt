package com.example.agroeasy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PesticidesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList: MutableList<Product> = mutableListOf()

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pesticides")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesticides)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the context and empty list
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        fetchProductData() // Fetch product data from Firebase
    }

    // Fetch product data from Firebase Realtime Database
    private fun fetchProductData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear() // Clear any existing data
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged() // Notify adapter to update UI
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data: ${error.message}")
            }
        })
    }
}
