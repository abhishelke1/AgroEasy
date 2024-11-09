package com.example.agroeasy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserProductAdapter(
    private val context: Context,
    private val userProducts: List<UserProduct>,
    private val onReadMoreClick: (UserProduct) -> Unit
) : RecyclerView.Adapter<UserProductAdapter.UserProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return UserProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserProductViewHolder, position: Int) {
        val userProduct = userProducts[position]
        holder.bind(userProduct)
    }

    override fun getItemCount(): Int = userProducts.size

    inner class UserProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivProductImage1: ImageView = itemView.findViewById(R.id.ivProductImage1)
        private val ivProductImage2: ImageView = itemView.findViewById(R.id.ivProductImage2)
        private val ivProductImage3: ImageView = itemView.findViewById(R.id.ivProductImage3)
        private val btnReadMore: TextView = itemView.findViewById(R.id.btnReadMore)

        fun bind(userProduct: UserProduct) {
            tvTitle.text = userProduct.title
            tvDescription.text = userProduct.description

            // Load product images with Glide
            if (userProduct.photoUrls.isNotEmpty()) {
                Glide.with(context).load(userProduct.photoUrls[0]).into(ivProductImage1)
                if (userProduct.photoUrls.size > 1) {
                    Glide.with(context).load(userProduct.photoUrls[1]).into(ivProductImage2)
                }
                if (userProduct.photoUrls.size > 2) {
                    Glide.with(context).load(userProduct.photoUrls[2]).into(ivProductImage3)
                }
            }

            // Set click listener for "Read More" button
            btnReadMore.setOnClickListener {
                onReadMoreClick(userProduct)
            }
        }
    }
}
