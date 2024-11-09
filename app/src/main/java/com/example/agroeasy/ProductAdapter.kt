package com.example.agroeasy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(private val products: MutableList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    // Method to add a new product to the list
    fun addProduct(newProduct: Product) {
        products.add(0, newProduct) // Add the new product at the top
        notifyDataSetChanged() // Notify the adapter to update the list
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.tvUserName)
        private val uploadTime: TextView = itemView.findViewById(R.id.tvUploadTime)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val description: TextView = itemView.findViewById(R.id.tvDescription)
        private val imageView1: ImageView = itemView.findViewById(R.id.ivProductImage1)
        private val imageView2: ImageView = itemView.findViewById(R.id.ivProductImage2)
        private val imageView3: ImageView = itemView.findViewById(R.id.ivProductImage3)
        private val profilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicture)

        fun bind(product: Product) {
            userName.text = product.sellerName
            uploadTime.text = formatTimestamp(product.timestamp)
            title.text = product.title
            description.text = product.description

            // Load product images (use Glide or any other image loading library)
            product.photoUrls?.let { photoUrls ->
                if (photoUrls.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(photoUrls[0])
                        .placeholder(R.drawable.placeholder) // Add a placeholder image
                        .into(imageView1)
                }
                if (photoUrls.size > 1) {
                    Glide.with(itemView.context)
                        .load(photoUrls[1])
                        .placeholder(R.drawable.placeholder) // Add a placeholder image
                        .into(imageView2)
                }
                if (photoUrls.size > 2) {
                    Glide.with(itemView.context)
                        .load(photoUrls[2])
                        .placeholder(R.drawable.placeholder) // Add a placeholder image
                        .into(imageView3)
                }
            }

            // Bind profile picture and name
            Glide.with(itemView.context)
                .load(product.profilePictureUrl) // Load the seller's profile picture
                .placeholder(R.drawable.img_24) // Add a default image in case of failure
                .into(profilePicture)
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }
}