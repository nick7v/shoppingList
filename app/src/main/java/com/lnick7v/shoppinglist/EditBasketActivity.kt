package com.lnick7v.shoppinglist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class EditBasketActivity : AppCompatActivity() {
    private lateinit var editTextBasketName: EditText
    private lateinit var editTextDateOfBasket: EditText
    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton
    private lateinit var buttonSave: Button
    private lateinit var viewModel: EditBasketViewModel
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var recyclerViewProducts: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
        viewModel = ViewModelProvider(this).get(EditBasketViewModel::class.java)
        initViews()

        productsAdapter = ProductsAdapter()
        recyclerViewProducts.adapter = productsAdapter

        viewModel.getProducts().observe(this) { products ->
            productsAdapter.setProducts(products)
        }

        viewModel.addProduct(Product("вобла", 100.50, 1))
        viewModel.addProduct(Product("мясо", 200.50, 1))

        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper
            .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val product = productsAdapter.getProducts()[position]
                viewModel.removeProduct(product)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerViewProducts)



        viewModel.getCloseScreen().observe(this) { closeScreen ->
            if(closeScreen) finish()
        }

        buttonSave.setOnClickListener {
            saveBasket()
        }
    }

    private fun saveBasket() {
        if (editTextBasketName.text.isEmpty() || editTextDateOfBasket.text.isEmpty()) {
            Toast.makeText(this, "Укажите название и дату", Toast.LENGTH_SHORT).show()
        } else {
            val basket = Basket(
                editTextBasketName.text.toString().trim(),
                getPriority(),
                editTextDateOfBasket.text.toString().trim())

            viewModel.addBasket(basket)
        }
    }

    private fun getPriority() = when {
        radioButtonLow.isChecked -> 0
        radioButtonMedium.isChecked -> 1
        else -> 2
    }

    private fun initViews() {
        editTextBasketName = findViewById(R.id.editTextBasketName)
        editTextDateOfBasket = findViewById(R.id.editTextDateOfBasket)
        radioButtonLow = findViewById(R.id.radioButtonLow)
        radioButtonMedium = findViewById(R.id.radioButtonMedium)
        radioButtonHigh = findViewById(R.id.radioButtonHigh)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerViewProducts = findViewById(R.id.recyclerViewProduct)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, EditBasketActivity::class.java)
        }
    }
}