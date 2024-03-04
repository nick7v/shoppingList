package com.lnick7v.shoppinglist

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapter:RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private var products = listOf<Product>()
    private var onProductFocusChangeListener: OnProductFocusChangeListener? = null

    fun setOnProductFocusChangeListener(onProductFocusChangeListener: OnProductFocusChangeListener) {
        this.onProductFocusChangeListener = onProductFocusChangeListener
    }

    fun getProducts(): List<Product> {
        return ArrayList(products)
    }

    fun setProducts(products: List<Product>) {
        this.products = products
        //notifyDataSetChanged()
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var editTextProductName: EditText = view.findViewById(R.id.editTextProductName)
        var textViewProductPrice: TextView = view.findViewById(R.id.textViewProductPrice)
       /* init {
            this.editTextProductName.doAfterTextChanged { finalText ->
                Toast.makeText(this, "dfgdfh", Toast.LENGTH_SHORT)
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

/*    override fun onViewAttachedToWindow(holder: ProductsViewHolder) {
        super.onViewAttachedToWindow(holder)
    }*/

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position]
        holder.editTextProductName.setText(product.name)
        holder.textViewProductPrice.text = product.price.toString()
        //if (position == products.size - 1) { //If it's a last item in RV
            holder.editTextProductName.setOnFocusChangeListener { view, hasFocus ->
                if (onProductFocusChangeListener != null) {
                    onProductFocusChangeListener!!.onProductFocusChange(product, position, view, hasFocus, products.size)
                    //productAddTextChangedListener!!.afterTextChanged(it!!, position) //NEED TO TEST it!!!!!!!!!!
                }
            }
        //}
    }

    interface OnProductFocusChangeListener {
        fun onProductFocusChange(product: Product, position: Int, view: View, hasFocus: Boolean, productsSize: Int) { }
    }
}