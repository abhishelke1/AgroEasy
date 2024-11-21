package com.example.agroeasy

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream

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
                // Set onClickListener to open the image in full screen
                imageViews[i].setOnClickListener {
                    openImageViewer(product.productImageUrls[i])
                }
            } else {
                imageViews[i].visibility = View.GONE // Hide unused image views
            }
        }

        // Handle save button click (toggle saved state)
        holder.saveButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                val savedProductsRef = userRef.child("savedProducts")
                val productId = product.timestamp.toString() // Unique identifier for the product

                // Check if the product is already saved
                savedProductsRef.child(productId).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // If the product is already saved, remove it
                        savedProductsRef.child(productId).removeValue().addOnCompleteListener {
                            if (it.isSuccessful) {
                                // Update UI (change icon to unsaved)
                                holder.saveButtonIcon.setImageResource(R.drawable.ic_save)
                                holder.saveButtonText.text = "Save"
                                Toast.makeText(context, "Product removed from saved list", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to remove product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // If the product is not saved, add it
                        savedProductsRef.child(productId).setValue(product).addOnCompleteListener {
                            if (it.isSuccessful) {
                                // Update UI (change icon to saved)
                                holder.saveButtonIcon.setImageResource(R.drawable.ic_saved)
                                holder.saveButtonText.text = "Saved"
                                Toast.makeText(context, "Product saved!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to save product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        // Check if the product is already saved, and update the UI accordingly
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val savedProductsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(user.uid).child("savedProducts")
            savedProductsRef.child(product.timestamp.toString()).get().addOnSuccessListener {
                if (it.exists()) {
                    holder.saveButtonIcon.setImageResource(R.drawable.ic_saved)
                    holder.saveButtonText.text = "Saved"
                } else {
                    holder.saveButtonIcon.setImageResource(R.drawable.ic_save)
                    holder.saveButtonText.text = "Save"
                }
            }
        }

        // Handle share button click
        holder.shareButton.setOnClickListener {
            shareProduct(product, holder)
        }
    }

    override fun getItemCount(): Int = productList.size

    private fun openImageViewer(imageUrl: String) {
        val intent = Intent(context, ImageViewerActivity::class.java).apply {
            putExtra("imageUrl", imageUrl)  // Passing the image URL to the ImageViewerActivity
        }
        context.startActivity(intent)
    }

    private fun shareProduct(product: Product, holder: ProductViewHolder) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this product:\n\n" +
                    "Title: ${product.title}\n" +
                    "Price: ${product.price}\n" +
                    "Address: ${product.address}\n\n" +
                    "Description: ${product.description}\n\n" +
                    "Download our app for more products: https://drive.google.com/file/d/1-DzbhWIq4aQWIgYGfIfZyPhkiM4iAVVe/view?usp=drivesdk")
        }

        // Download images and attach them
        val imagesToShare = mutableListOf<Uri>()
        val imageViews = listOf(
            holder.productImageView,
            holder.productImageView2,
            holder.productImageView3,
            holder.productImageView4
        )

        for (i in product.productImageUrls.indices) {
            val bitmap = getBitmapFromImageView(imageViews[i])
            bitmap?.let {
                val imageUri = saveImageToFile(it)
                imageUri?.let { uri -> imagesToShare.add(uri) }
            }
        }

        if (imagesToShare.isNotEmpty()) {
            shareIntent.apply {
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imagesToShare))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Product"))
    }

    private fun getBitmapFromImageView(imageView: ImageView): Bitmap? {
        imageView.isDrawingCacheEnabled = true
        return imageView.drawingCache
    }

    private fun saveImageToFile(bitmap: Bitmap): Uri? {
        return try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_image_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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

        // Save Button
        val saveButton: View = itemView.findViewById(R.id.saveButton)
        val saveButtonIcon: ImageView = itemView.findViewById(R.id.saveButtonIcon)
        val saveButtonText: TextView = itemView.findViewById(R.id.saveButtonText)

        // Share Button
        val shareButton: View = itemView.findViewById(R.id.shareButton)
    }
}
