package com.example.agroeasy

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Saved : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var savedProductList: ArrayList<Product>
    private lateinit var savedProductAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)

        recyclerView = findViewById(R.id.recyclerViewMyProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        savedProductList = ArrayList()

        savedProductAdapter = ProductAdapter(this, savedProductList)
        recyclerView.adapter = savedProductAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            fetchSavedProducts()
        }

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

    }

    private fun fetchSavedProducts() {
        databaseReference.child("savedProducts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedProductList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        savedProductList.add(it)
                    }
                }
                savedProductAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }
}
