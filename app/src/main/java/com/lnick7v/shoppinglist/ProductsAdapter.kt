package com.lnick7v.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapter:RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private var products = listOf<Product>()

    fun getProducts(): List<Product> {
        return ArrayList(products)
    }

    fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var editTextProductName: EditText = view.findViewById(R.id.editTextProductName)
        var textViewProductPrice: TextView = view.findViewById(R.id.textViewProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position]
        holder.editTextProductName.setText(product.name)
        holder.textViewProductPrice.text = product.price.toString()
    }
}