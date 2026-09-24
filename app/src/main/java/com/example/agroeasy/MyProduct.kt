package com.example.agroeasy
data class MyProduct(
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val price: Double = 0.0,
    val uploaderId: String = "",
    val photos: List<String> = listOf(),
    val timestamp: Long = 0,
    val status: String = "",
    val username: String = "",
    val uploadTime: String = "",
    val profileImage: String = "" // Add profileImage URL here
)
