package com.lnick7v.shoppinglist

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private var products = listOf<Product>()
    private var onProductFocusChangeListener: OnProductFocusChangeListener? = null
    private var onAddProductClickListener: OnAddProductClickListener? = null
    private var onProductKeyListener: OnProductKeyListener? = null


    fun setOnProductFocusChangeListener(onProductFocusChangeListener: OnProductFocusChangeListener) {
        this.onProductFocusChangeListener = onProductFocusChangeListener
    }

    fun setOnAddProductClickListener(onAddProductClickListener: OnAddProductClickListener) {
        this.onAddProductClickListener = onAddProductClickListener
    }

    fun setOnProductKeyListener(onProductKeyListener: OnProductKeyListener) {
        this.onProductKeyListener = onProductKeyListener
    }

    fun getProducts(): List<Product> {
        return ArrayList(products)
    }

    fun setProducts(products: List<Product>) {
        this.products = products
    }

    class ProductsViewHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
        lateinit var editTextProductName: EditText
        lateinit var textViewProductPrice: TextView
        lateinit var buttonAddProduct: FloatingActionButton
        init { // throw exception: findViewById(...) must not be null, if don't check type of view in init - it's strange
            if (viewType == R.layout.product_item) {
                editTextProductName = view.findViewById(R.id.editTextProductName)
                textViewProductPrice = view.findViewById(R.id.textViewProductPrice)
            } else if (viewType == R.layout.button_item) {
                buttonAddProduct = view.findViewById(R.id.buttonAddProduct)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view =
            if (viewType == R.layout.product_item) {
                LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
            } else {
                LayoutInflater.from(parent.context).inflate(R.layout.button_item, parent, false)
            }
        return ProductsViewHolder(view, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == products.size) R.layout.button_item
        else R.layout.product_item
    }

    override fun getItemCount(): Int {
        return products.size + 1
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        if (position == products.size) { //when item is add button (last item)
            holder.buttonAddProduct.setOnClickListener {
                if (onAddProductClickListener != null) {
                    onAddProductClickListener!!.onAddProductClick(products.size)
                }
            }
        } else { //when item is product
           val product = products[position]
            holder.editTextProductName.setText(product.name)
            holder.textViewProductPrice.text = product.price.toString()
            holder.editTextProductName.setOnFocusChangeListener { view, hasFocus ->
                if (onProductFocusChangeListener != null) {
                    onProductFocusChangeListener!!.onProductFocusChange(product, view, hasFocus)
                    //productAddTextChangedListener!!.afterTextChanged(it!!, position) //NEED TO TEST it!!!!!!!!!!
                }
            }
            holder.editTextProductName.setOnKeyListener { view, keyCode, event ->
                if (onProductKeyListener != null /*&& position == products.size - 1*/) {
                    onProductKeyListener!!.onProductKeyListener(products.size, keyCode, event, position)
                }
                false
            }
        }
    }

    interface OnProductFocusChangeListener {
        fun onProductFocusChange(product: Product, view: View, hasFocus: Boolean) {
        }
    }

    interface OnAddProductClickListener {
        fun onAddProductClick(productsSize: Int){}
    }

    interface OnProductKeyListener {
        fun onProductKeyListener(productsSize: Int, keyCode: Int, event: KeyEvent, position: Int): Boolean { return false }
    }
}