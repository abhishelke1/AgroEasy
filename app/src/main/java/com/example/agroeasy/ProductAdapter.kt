package com.example.agroeasy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductAdapter(
    private val context: Context,
    private val productList: List<Product> // Receive the list from the activity
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvTitle.text = product.title
        holder.tvDescription.text = product.description
        holder.productPrice.text = product.price
        holder.userAdress.text = product.userAdress
        holder.userNumber.text = product.userNumber
        holder.userEmail.text = product.userEmail

        // Load images from Firebase Storage using Glide
        loadImageFromStorage(product.productImage1, holder.ivProductImage1)
        loadImageFromStorage(product.productImage2, holder.ivProductImage2)
        loadImageFromStorage(product.productImage3, holder.ivProductImage3)
        loadImageFromStorage(product.productImage4, holder.ivProductImage4)
    }

    override fun getItemCount() = productList.size

    // ViewHolder class to bind views
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val userAdress: TextView = itemView.findViewById(R.id.UserAdress)
        val userNumber: TextView = itemView.findViewById(R.id.UserNumber)
        val userEmail: TextView = itemView.findViewById(R.id.Useremail)
        val ivProductImage1: ImageView = itemView.findViewById(R.id.ivProductImage1)
        val ivProductImage2: ImageView = itemView.findViewById(R.id.ivProductImage2)
        val ivProductImage3: ImageView = itemView.findViewById(R.id.ivProductImage3)
        val ivProductImage4: ImageView = itemView.findViewById(R.id.ivProductImage4)
    }

    // Function to load images from Firebase Storage into ImageView
    private fun loadImageFromStorage(imageUrl: String, imageView: ImageView) {
        val imageRef = storageReference.child("images/$imageUrl")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            Log.e("FirebaseStorage", "Error loading image", exception)
        }
    }
}
