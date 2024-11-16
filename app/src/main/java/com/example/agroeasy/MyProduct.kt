package com.example.agroeasy
data class MyProduct(
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val price: String = "",
    val uploaderId: String = "",
    val uploaderName: String = "",
    val profilePhotoUrl: String = "",
    val photos: List<String> = emptyList()
)
