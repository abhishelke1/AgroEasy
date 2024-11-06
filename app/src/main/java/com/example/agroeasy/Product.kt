package com.example.agroeasy

data class Product(
    val uploaderName: String = "",
    val uploaderProfileImage: String = "",
    val postTime: String = "",
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList()
)
