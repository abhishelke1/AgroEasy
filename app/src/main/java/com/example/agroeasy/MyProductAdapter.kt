package com.example.agroeasy
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class MyProductAdapter(private val context: Context, private val myProductList: List<MyProduct>) :
    RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_product, parent, false)
        return MyProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        val product = myProductList[position]
        holder.productTitle.text = product.title
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price
        holder.productAddress.text = product.address
        Picasso.get().load(product.profilePhotoUrl).into(holder.profileImage)

        // You can add more logic for displaying images and other fields if needed
    }

    override fun getItemCount(): Int {
        return myProductList.size
    }

    inner class MyProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productTitle: TextView = view.findViewById(R.id.productTitle)
        val productDescription: TextView = view.findViewById(R.id.productDescription)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productAddress: TextView = view.findViewById(R.id.productAddress)
        val profileImage: ImageView = view.findViewById(R.id.productProfileImage)
    }
}
