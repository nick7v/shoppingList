package com.lnick7v.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

const val BASKET_ID = "idBasket"

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

    private var basketID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
        viewModel = ViewModelProvider(this)[EditBasketViewModel::class.java]
        initViews()
        selectActivityStartMode()  // edit existing or create new basket


        productsAdapter = ProductsAdapter()
        recyclerViewProducts.adapter = productsAdapter

        viewModel.getProducts(basketID).observe(this) { products ->
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
                basketID,
                editTextBasketName.text.toString().trim(),
                getPriority(),
                editTextDateOfBasket.text.toString().trim())

            viewModel.addBasket(basket)
        }
    }


    private fun selectActivityStartMode() {
        basketID = intent.getIntExtra(BASKET_ID, -1)
        if(basketID != -1) { //Edit an existing basket
            startActivityInEditBasketMode(basketID)
        } else { //Create a new basket
            basketID = viewModel.getNewBasketId()
        }
    }

    private fun startActivityInEditBasketMode(idBasket: Int) {
        val basket = viewModel.editBasket(idBasket)
        editTextBasketName.setText(basket.name)
        editTextDateOfBasket.setText(basket.date)
        setPriority(basket.priority)

    }


    private fun setPriority(priority: Int) {
        val listOfPriorityradioButton = listOf(
            radioButtonLow,
            radioButtonMedium,
            radioButtonHigh
        )
        listOfPriorityradioButton[priority].isChecked = true ///изменятся ли автоматом остальные?????????
        //val radioGroup = findViewById<RadioGroup>(R.id.radioGroupPriorityBasket)
        //radioGroup.check(radioButtonHigh.id)
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
        fun newCreateIntent(context: Context): Intent {
            return Intent(context, EditBasketActivity::class.java)
        }

        fun newEditIntent(context: Context, idBasket: Int): Intent {
            val intent = Intent(context, EditBasketActivity::class.java)
            intent.putExtra(BASKET_ID, idBasket)
            return intent
        }
    }
}