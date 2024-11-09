package com.example.agroeasy

data class UserProduct(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val uploaderId: String = "",
    val photoUrls: List<String> = emptyList(),
    val timestamp: Long = 0L
)
