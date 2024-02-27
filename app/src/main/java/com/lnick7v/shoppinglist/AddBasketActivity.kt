package com.lnick7v.shoppinglist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddBasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
    }

    private fun initViews() {

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddBasketActivity::class.java)
        }
    }
}