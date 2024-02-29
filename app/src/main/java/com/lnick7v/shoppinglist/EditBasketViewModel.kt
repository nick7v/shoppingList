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

    fun addProduct(product: Product) {
        Thread { basketsProductsDatabase.ProductsDao().add(product) }.start()
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

    fun getNewBasketId(): Int {
        var id: Int? = null
        val t1 = Thread { id = basketsProductsDatabase.BasketDao().getBasketsDBSize() }
        t1.start()
        t1.join()
        return (id!! + 1)
    }

    fun addBasket(basket: Basket) {
        Thread {
            basketsProductsDatabase.BasketDao().add(basket)
            shouldCloseScreen.postValue(true)
        }.start()
    }
}