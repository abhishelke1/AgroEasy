package com.example.agroeasy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Context
class ProductAdapter(private val context: Context, private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.title.text = product.title
        holder.description.text = product.description
        holder.address.text = product.address
        holder.price.text = product.price
        holder.userName.text = product.userName
        holder.uploadTime.text = product.uploadTime

        // Load profile image using Glide
        Glide.with(context).load(product.profileImageUrl).into(holder.profileImageView)

        // Load up to 4 product images
        val imageViews = listOf(
            holder.productImageView,
            holder.productImageView2,
            holder.productImageView3,
            holder.productImageView4
        )

        for (i in imageViews.indices) {
            if (i < product.productImageUrls.size) {
                imageViews[i].visibility = View.VISIBLE
                Glide.with(context).load(product.productImageUrls[i]).into(imageViews[i])
            } else {
                imageViews[i].visibility = View.GONE // Hide unused image views
            }
        }
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.productTitle)
        val description: TextView = itemView.findViewById(R.id.productDescription)
        val address: TextView = itemView.findViewById(R.id.productAddress)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val userName: TextView = itemView.findViewById(R.id.productUserNam)
        val uploadTime: TextView = itemView.findViewById(R.id.productUploadTim)
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImag)

        // References for up to 4 product images
        val productImageView: ImageView = itemView.findViewById(R.id.productImage)
        val productImageView2: ImageView = itemView.findViewById(R.id.productImage2)
        val productImageView3: ImageView = itemView.findViewById(R.id.productImage3)
        val productImageView4: ImageView = itemView.findViewById(R.id.productImage4)
    }
}