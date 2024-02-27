package com.lnick7v.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val basketsProductsDatabase = BasketsProductsDatabase.getInstance(application)

    fun getBaskets(): LiveData<List<Basket>> {
        return basketsProductsDatabase.BasketDao().getBaskets()
    }

    fun remove (basket: Basket) {
        Thread {basketsProductsDatabase.BasketDao().remove(basket.id)}.start()
    }
}