package com.example.agroeasy

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class market : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var productTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market) // Match your XML file name

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("ProductRates")

        // Find the table layout
        productTable = findViewById(R.id.productTable)

        // Load product rates
        loadProductRates()
    }

    private fun loadProductRates() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the table first (in case of reloading)
                productTable.removeViews(1, productTable.childCount - 1)

                for (productSnapshot in snapshot.children) {
                    val productName = productSnapshot.key
                    val minRate = productSnapshot.child("minRate").getValue(Double::class.java)
                    val maxRate = productSnapshot.child("maxRate").getValue(Double::class.java)

                    if (productName != null && minRate != null && maxRate != null) {
                        addTableRow(productName, minRate, maxRate)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@market, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTableRow(productName: String, minRate: Double, maxRate: Double) {
        val row = TableRow(this)

        // Create TextViews for each column
        val nameView = TextView(this)
        nameView.text = productName
        nameView.setPadding(8, 8, 8, 8)
        nameView.gravity = android.view.Gravity.CENTER

        val minRateView = TextView(this)
        minRateView.text = minRate.toString()
        minRateView.setPadding(8, 8, 8, 8)
        minRateView.gravity = android.view.Gravity.CENTER

        val maxRateView = TextView(this)
        maxRateView.text = maxRate.toString()
        maxRateView.setPadding(8, 8, 8, 8)
        maxRateView.gravity = android.view.Gravity.CENTER

        // Add columns to the row
        row.addView(nameView)
        row.addView(minRateView)
        row.addView(maxRateView)

        // Add the row to the table
        productTable.addView(row)
    }
}
