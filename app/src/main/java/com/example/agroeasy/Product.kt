package com.example.agroeasy

data class Product(
    val sellerName: String = "",
    val description: String = "",
    val title: String = "",
    val timestamp: Long = 0L,
    val photoUrls: List<String>? = null, // List of image URLs for the product
    val profilePictureUrl: String = "" // URL of the seller's profile picture
)
