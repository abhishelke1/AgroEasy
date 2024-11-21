package com.example.agroeasy
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class MyProductAdapter(private val context: Context, private val products: List<MyProduct>) :
    RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return MyProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImag)
        private val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        private val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productAddress: TextView = itemView.findViewById(R.id.productAddress)
        private val productUserName: TextView = itemView.findViewById(R.id.productUserNam)
        private val productUploadTime: TextView = itemView.findViewById(R.id.productUploadTim)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productImage2: ImageView = itemView.findViewById(R.id.productImage2)
        private val productImage3: ImageView = itemView.findViewById(R.id.productImage3)
        private val productImage4: ImageView = itemView.findViewById(R.id.productImage4)

        fun bind(product: MyProduct) {
            productTitle.text = product.title
            productDescription.text = product.description
            productPrice.text = "Price: ${product.price}"
            productAddress.text = "Address: ${product.address}"
            productUserName.text = product.username
            productUploadTime.text = product.uploadTime

            // Load images (assuming product.photos contains URLs)
            Glide.with(itemView.context)
                .load(product.photos.getOrNull(0)) // Load first image
                .into(productImage)

            Glide.with(itemView.context)
                .load(product.photos.getOrNull(1)) // Load second image
                .into(productImage2)

            Glide.with(itemView.context)
                .load(product.photos.getOrNull(2)) // Load third image
                .into(productImage3)

            Glide.with(itemView.context)
                .load(product.photos.getOrNull(3)) // Load fourth image
                .into(productImage4)

            // Optional: Load profile image (you can set a placeholder if needed)
            Glide.with(itemView.context)
                .load(product.profileImage) // Use the user's profile image URL
                .into(profileImage)
        }
    }
}
