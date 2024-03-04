package com.lnick7v.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var refresh: FloatingActionButton  //!!!!!!!!!!!!!ВРЕМЕННО

    private var basketID: Int = -1 // -1 - default value for creating new basket Activity mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
        viewModel = ViewModelProvider(this)[EditBasketViewModel::class.java]
        initViews()
        selectActivityStartMode()  // edit existing or create new basket

        productsAdapter = ProductsAdapter()

        /* productsAdapter.setProductAddTextChangedListener(object: ProductsAdapter.ProductAddTextChangedListener{
             override fun afterTextChanged(text: Editable, position: Int) {
                 //Toast.makeText(this@EditBasketActivity, text, Toast.LENGTH_SHORT).show()//!!!!!!!!!!!!!!!!!!!!!
                 if (viewModel.getProductsSize() == position + 1) {
                     viewModel.addProduct(Product("$position", 0.0, basketID))
                 }
             }
         } )*/
        productsAdapter.setOnProductFocusChangeListener(object :
            ProductsAdapter.OnProductFocusChangeListener {
            override fun onProductFocusChange(
                product: Product,
                position: Int,
                view: View,
                hasFocus: Boolean,
                productsSize: Int
            ) {
                if (position == productsSize - 1) {
                    if (hasFocus) {
                        viewModel.addEmptyProductToEnd(basketID)
                        //***** КОСТЫЛЬ, нужно вызывать notifyItemChanged в другом месте, после обновления БД в адаптере
                        Thread {
                            Thread.sleep(1000)
                            //handler.post { productsAdapter.notifyItemInserted(position + 1) } // выходит исключение
                            handler.post { productsAdapter.notifyItemRangeChanged(position + 1, productsSize + 1) }  //productsAdapter.getItemCount()
                        }.start()
                        //*************************
                        Log.d("product", "ADD EMPTY PRODUCT id ${product.id}")
                    }
                }
                if (!hasFocus) {
                    val newName = (view as EditText).text.toString().trim()
                    viewModel.updateProduct(newName, product.id)
                    //*********ВРОДЕ СТАЛ РАБОТАТЬ И БЕЗ ЭТОГО КОСТЫЛЯ
                    //***** КОСТЫЛЬ, нужно вызывать notifyItemChanged в другом месте, после обновления БД в адаптере
                    /*Thread {
                        Thread.sleep(2000)
                        productsAdapter.notifyItemChanged(position)
                    }.start()*/
                    //*************************
                    Log.d("product", "UPDATE PRODUCT id ${product.id} new name: ${newName}")
                }
            }
        })

        recyclerViewProducts.adapter = productsAdapter

        viewModel.getProducts(basketID).observe(this) { products ->
           productsAdapter.setProducts(products)
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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
                //***** КОСТЫЛЬ, нужно вызывать notifyItemRemoved в другом месте, после обновления БД в адаптере
                Thread {
                    Thread.sleep(500)
                    handler.post { productsAdapter.notifyItemRemoved(position) }
                    handler.post { productsAdapter.notifyItemRangeChanged(position, productsAdapter.getItemCount()) }
                }.start()
                //*************************
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerViewProducts)



        viewModel.getCloseScreen().observe(this) { closeScreen ->
            if (closeScreen) finish()
        }

        buttonSave.setOnClickListener {
            saveBasket()
        }

        editTextBasketName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) productsAdapter.notifyItemRangeChanged(0, productsAdapter.itemCount)
        }

        refresh.setOnClickListener {
            productsAdapter.notifyDataSetChanged()
        }
    }

    private fun saveBasket() {
        if (editTextBasketName.text.isEmpty() || editTextDateOfBasket.text.isEmpty()) {
            Toast.makeText(this, "Укажите название и дату", Toast.LENGTH_SHORT).show()
        } else {
            val name = editTextBasketName.text.toString().trim()
            val date = editTextDateOfBasket.text.toString().trim()
            viewModel.updateBasket(name, date, getPriority(), basketID)
        }
    }


    private fun selectActivityStartMode() {
        basketID = intent.getIntExtra(BASKET_ID, -1)
        if (basketID != -1) { //Edit an existing basket mode
            startActivityInEditBasketMode(basketID)
        } else { //Create a new basket
            basketID = viewModel.addBasket(Basket("", getPriority(), ""))
            viewModel.addEmptyProductToEnd(basketID)
            // TODO(удаление имеющихся продуктов из БД, если корзина не была создана)
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
        listOfPriorityradioButton[priority].isChecked = true
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
        refresh = findViewById(R.id.refresh)  ///!!!!!! TEMPORARY
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