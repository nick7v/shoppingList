package com.lnick7v.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EditBasketViewModel(application: Application) : AndroidViewModel(application) {
    private val basketsProductsDatabase = BasketsProductsDatabase.getInstance(application)
    private var shouldCloseScreen = MutableLiveData<Boolean>()

    fun getProducts(): LiveData<List<Product>> {
        return basketsProductsDatabase.ProductsDao().getProducts()
    }

    fun addProduct(product: Product) {
        Thread { basketsProductsDatabase.ProductsDao().add(product) }.start()
    }

    fun removeProduct(product: Product) {
        Thread { basketsProductsDatabase.ProductsDao().remove(product.id) }
    }


    fun getCloseScreen(): LiveData<Boolean> = shouldCloseScreen

    fun addBasket(basket: Basket) {
        Thread {
            basketsProductsDatabase.BasketDao().add(basket)
            shouldCloseScreen.postValue(true)
        }.start()
    }
}