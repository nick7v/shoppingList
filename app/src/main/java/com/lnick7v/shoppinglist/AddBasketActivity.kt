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

class AddBasketActivity : AppCompatActivity() {
    private lateinit var editTextBasketName: EditText
    private lateinit var editTextDateOfBasket: EditText
    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton
    private lateinit var buttonSave: Button
    private lateinit var viewModel: AddBasketViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
        viewModel = ViewModelProvider(this).get(AddBasketViewModel::class.java)
        initViews()

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
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddBasketActivity::class.java)
        }
    }
}