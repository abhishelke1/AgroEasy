package com.example.agroeasy
data class Product(
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val price: String = "",  // You may want to store it as String if you store price as a string in Firebase
    val userName: String = "",
    val uploadTime: String = "",  // This can remain a String after converting the timestamp
    val profileImageUrl: String = "",
    val productImageUrls: List<String> = emptyList(),
    val timestamp: Long = 0L  // Make sure timestamp is Long in the model
)
