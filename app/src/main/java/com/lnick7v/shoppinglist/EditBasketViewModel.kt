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
    /*fun getProducts(idBasket: Int): LiveData<List<Product>> {
        if (isItNewBasket) {
            addEmptyProductToEnd(idBasket)
        } else {
            val t1 = Thread {
                tempProductList.postValue(
                    basketsProductsDatabase.ProductsDao().getProducts(idBasket)
                )
            }
            t1.start()
            t1.join()
        }
        return tempProductList
    }*/

    //fun getProductsSize(): Int = tempProductList.value?.toMutableList()?.size ?: 0

    fun addProduct(product: Product) {
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

    /*fun getNewBasketId(): Int { //TODO(ID ФОРМИРОВАТЬ НЕ ИСХОДЯ ИЗ SIZE А ИСХОДЯ ИЗ ПОСЛЕДНЕГО ID!!!!!!!!!!) ИЛИ ЗАМЕНИТЬ НА АВТО ФОРМИРОВАНИЕ
        var id: Int? = null
        val t1 = Thread { id = basketsProductsDatabase.BasketDao().getBasketsDBSize() }
        t1.start()
        t1.join()
        isItNewBasket = true
        return (id!! + 1)
    }*/

    fun addBasket(basket: Basket): Int {
        var id: Long? = null
        val t1 = Thread {
            id = basketsProductsDatabase.BasketDao().add(basket)
        }
        t1.start()
        t1.join()
        return id!!.toInt()
    }

    fun updateBasket(name: String, date: String, priority: Int, id: Int) {
        Thread {
            basketsProductsDatabase.BasketDao().updateBasket(name, date, priority, id)
            shouldCloseScreen.postValue(true)
        }.start()
    }
}