package com.example.agroeasy

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MyProductActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myProductList: ArrayList<MyProduct>
    private lateinit var myProductAdapter: MyProductAdapter
    private lateinit var backButton: ImageButton
    private lateinit var titleText: TextView

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_product)

        recyclerView = findViewById(R.id.recyclerViewMyProducts)
        backButton = findViewById(R.id.backButton)
        titleText = findViewById(R.id.title)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        myProductList = ArrayList()
        myProductAdapter = MyProductAdapter(this, myProductList)
        recyclerView.adapter = myProductAdapter

        // Load the user's products from Firebase
        loadUserProducts()

        // Back button click listener
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Query Firebase to fetch products uploaded by the user
        val userProductsRef = database.child("products").orderByChild("uploaderId").equalTo(userId)

        userProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    myProductList.clear() // Clear the list before adding new data
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(MyProduct::class.java)
                        if (product != null) {
                            myProductList.add(product) // Add the product to the list
                        }
                    }
                    myProductAdapter.notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
                } else {
                    Toast.makeText(this@MyProductActivity, "No products found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyProductActivity, "Failed to load products: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
