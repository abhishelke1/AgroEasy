package com.example.agroeasy

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var productTable: TableLayout
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace) // Replace with your XML layout file name

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("ProductRates")

        // Find views
        productTable = findViewById(R.id.productTable)
        submitButton = findViewById(R.id.btnSubmitRates)

        // Handle submit button click
        submitButton.setOnClickListener {
            saveProductRates()
        }
    }

    private fun saveProductRates() {
        val rates = mutableMapOf<String, Map<String, Any>>()

        // Loop through each row in the table (excluding the header)
        for (i in 1 until productTable.childCount) {
            val row = productTable.getChildAt(i) as TableRow

            // Extract product name, min rate, and max rate
            val productName = (row.getChildAt(0) as TextView).text.toString().trim()
            val minRateInput = (row.getChildAt(1) as EditText).text.toString().trim()
            val maxRateInput = (row.getChildAt(2) as EditText).text.toString().trim()

            // Validate inputs
            if (productName.isNotEmpty() && minRateInput.isNotEmpty() && maxRateInput.isNotEmpty()) {
                val minRate = minRateInput.toDoubleOrNull()
                val maxRate = maxRateInput.toDoubleOrNull()

                if (minRate != null && maxRate != null) {
                    // Add product details to rates map
                    rates[productName] = mapOf("minRate" to minRate, "maxRate" to maxRate)
                } else {
                    Toast.makeText(this, "Invalid rate values for $productName", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields for $productName", Toast.LENGTH_SHORT).show()
            }
        }

        // Save data to Firebase
        if (rates.isNotEmpty()) {
            database.setValue(rates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Rates saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save rates. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No valid data to save.", Toast.LENGTH_SHORT).show()
        }
    }
}
