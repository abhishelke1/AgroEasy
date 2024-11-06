package com.example.agroeasy

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val postTime: TextView = itemView.findViewById(R.id.tvUploadTime)
        val productTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val productDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val imageView1: ImageView = itemView.findViewById(R.id.ivProductImage1)
        val imageView2: ImageView = itemView.findViewById(R.id.ivProductImage2)
        val imageView3: ImageView = itemView.findViewById(R.id.ivProductImage3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Log the URLs to debug
        Log.d("ProductAdapter", "Profile Image URL: ${product.uploaderProfileImage}")
        Log.d("ProductAdapter", "Product Images: ${product.images.joinToString(", ")}")

        // Load profile image
        Glide.with(holder.itemView.context)
            .load(product.uploaderProfileImage ?: "default_image_url") // Provide a default image URL if null
            .placeholder(R.drawable.img_24) // Replace with your placeholder image
            .error(R.drawable.img_24) // Replace with your error image
            .circleCrop()
            .into(holder.profileImage)

        // Set user name and upload time
        holder.userName.text = product.uploaderName ?: "Unknown" // Handle null case
        holder.postTime.text = product.postTime ?: "N/A" // Handle null case

        // Set product title and description
        holder.productTitle.text = product.title
        holder.productDescription.text = product.description

        // Clear previous images and load new images
        holder.imageView1.setImageDrawable(null)
        holder.imageView2.setImageDrawable(null)
        holder.imageView3.setImageDrawable(null)

        // Load product images if available
        product.images.forEachIndexed { index, imageUrl ->
            val imageView = when (index) {
                0 -> holder.imageView1
                1 -> holder.imageView2
                2 -> holder.imageView3
                else -> null
            }
            imageView?.let {
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile) // Replace with your placeholder image
                    .error(R.drawable.ic_profile) // Replace with your error image
                    .centerCrop()
                    .into(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
