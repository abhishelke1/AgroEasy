package com.example.agroeasy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyProductActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userProductAdapter: UserProductAdapter
    private lateinit var databaseRef: DatabaseReference
    private val userProducts = mutableListOf<UserProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_product)

        recyclerView = findViewById(R.id.recyclerViewMyProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase references
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("products")

        // Initialize adapter with a lambda for the "Read More" button
        userProductAdapter = UserProductAdapter(this, userProducts) { userProduct ->
            Toast.makeText(this, "Read more for ${userProduct.title}", Toast.LENGTH_SHORT).show()
            // Implement "Read More" click action, e.g., navigate to detail page
        }
        recyclerView.adapter = userProductAdapter

        loadUserProducts(userId)
    }

    private fun loadUserProducts(userId: String?) {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseRef.orderByChild("uploaderId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userProducts.clear()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(UserProduct::class.java)
                        product?.let { userProducts.add(it) }
                    }
                    userProductAdapter.notifyDataSetChanged()
                    Log.d("MyProductActivity", "Loaded ${userProducts.size} products")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MyProductActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                    Log.e("MyProductActivity", "Database error: ${error.message}")
                }
            })
    }
}
