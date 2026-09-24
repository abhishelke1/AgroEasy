package com.example.agroeasy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.util.Log

class MyProductActivity : AppCompatActivity() {

    private lateinit var productList: ArrayList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_product)

        // Get the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserId = currentUser.uid
            Log.d("MyProductActivity", "Current User ID: $currentUserId") // Debugging the currentUserId
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("MyProductActivity", "Current User ID: $currentUserId") // Debugging the currentUserId

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productList = ArrayList()
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Initialize Firebase database reference to products
        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        // Query to get products uploaded by the current user
        databaseReference.orderByChild("userId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        productList.clear()
                        for (productSnapshot in snapshot.children) {
                            val product = productSnapshot.getValue(Product::class.java)
                            if (product != null) {
                                productList.add(product)
                            }
                        }
                        productAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@MyProductActivity, "No products found for this user", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MyProductActivity, "Failed to load products: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
