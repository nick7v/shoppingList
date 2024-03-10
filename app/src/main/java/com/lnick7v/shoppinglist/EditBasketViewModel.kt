package com.lnick7v.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EditBasketViewModel(application: Application) : AndroidViewModel(application) {
    private val basketsProductsDatabase = BasketsProductsDatabase.getInstance(application)
    private var shouldCloseScreen = MutableLiveData<Boolean>()

    fun getProducts(idBasket: Int): LiveData<List<Product>> {
        return basketsProductsDatabase.ProductsDao().getProducts(idBasket)
    }

    private fun addProduct(product: Product) {
        Thread { basketsProductsDatabase.ProductsDao().add(product) }.start()
    }

    fun addEmptyProductToEnd(idBasket: Int) {
        addProduct(Product("", 0.0, idBasket))
    }

    fun updateProduct(name: String, id: Int) {
        Thread { basketsProductsDatabase.ProductsDao().updateProduct(name, id) }.start()
    }

    fun removeProduct(product: Product) {
        Thread { basketsProductsDatabase.ProductsDao().remove(product.id) }.start()
    }


    fun getCloseScreen(): LiveData<Boolean> = shouldCloseScreen

    fun editBasket(idBasket: Int): Basket {
        var basket: Basket? = null
        val t1 = Thread { basket = basketsProductsDatabase.BasketDao().getOneBasket(idBasket) }
        t1.start()
        t1.join()
        return basket!!
    }

    fun addBasket(basket: Basket): Int {
        var id: Long? = null
        val t1 = Thread {
            id = basketsProductsDatabase.BasketDao().add(basket)
        }
        t1.start()
        t1.join()
        return id!!.toInt()
    }

    fun removeBasketAndExit(basketID: Int) {
        Thread {
            basketsProductsDatabase.BasketDao().remove(basketID)
            shouldCloseScreen.postValue(true)
        }.start()

    }

    fun updateBasket(name: String, date: String, priority: Int, id: Int) {
        Thread {
            basketsProductsDatabase.BasketDao().updateBasket(name, date, priority, id)
            shouldCloseScreen.postValue(true)
        }.start()
    }
}