package com.lnick7v.shoppinglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class EditBasketViewModel(application: Application) : AndroidViewModel(application) {
    private val basketsProductsDatabase = BasketsProductsDatabase.getInstance(application)
    private var shouldCloseScreen = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()

    fun getProducts(idBasket: Long): LiveData<List<Product>> {
        return basketsProductsDatabase.ProductsDao().getProducts(idBasket)
    }

    private fun addProduct(product: Product) {
        val disposable = basketsProductsDatabase.ProductsDao().add(product)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Action {
                override fun run() {
                    Log.d(
                        "EditBasketViewModel",
                        "Add product with id ${product.id} to basket with id ${product.basketId}"
                    )
                }
            }, object : Consumer<Throwable> {
                override fun accept(t: Throwable) {
                    Log.e("EditBasketViewModel", "Error: Add product ()")
                }
            })
        compositeDisposable.add(disposable)
    }


    fun addEmptyProductToEnd(idBasket: Long) {
        addProduct(Product("", 0.0, idBasket))
    }



    /**
     * The function is implemented only to update the product name !!!!!!!!!
     * TODO (you will have to improve the function when you implement other properties of the product)
     */
    fun updateProduct(name: String, id: Int) {
        val disposable = basketsProductsDatabase.ProductsDao().updateProduct(name, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("EditBasketViewModel", "Update: product with id $id")
            }) { Log.e("EditBasketViewModel", "Error: updateProduct()") }
        compositeDisposable.add(disposable)
    }


    fun removeProduct(product: Product) {
        val disposable = basketsProductsDatabase.ProductsDao().remove(product.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("EditBasketViewModel", "Remove: product with id ${product.id}")
            }) { Log.e("EditBasketViewModel", "Error: removeProduct()") }
        compositeDisposable.add(disposable)
    }


    fun getCloseScreen(): LiveData<Boolean> = shouldCloseScreen



    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // оцени правильность и адекватность реализации данных 2х методов getOneBasket и addBasket
    // с использованием rxJava в связке с соответствующими методами из BasketDao(): getOneBasket() и
    // add() и методами из EditBasketActivity: startActivityInEditBasketMode и startActivityInNewBasketMode
    fun getOneBasket(idBasket: Long): Single<Basket> {
        return basketsProductsDatabase.BasketDao().getOneBasket(idBasket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun addBasket(basket: Basket): Single<Long> {
        return basketsProductsDatabase.BasketDao().add(basket)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    fun removeBasketAndExit(basketID: Long) {
        val disposable = basketsProductsDatabase.BasketDao().remove(basketID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                shouldCloseScreen.value = true
                Log.d("EditBasketViewModel", "removeBasket: basket with id $basketID")
            })
            {
                Log.e("EditBasketViewModel", "Error: removeBasket() ${it.printStackTrace()}")
            }
        compositeDisposable.add(disposable)
    }

    fun updateBasket(name: String, date: String, priority: Int, id: Long) {
        val disposable = basketsProductsDatabase.BasketDao().updateBasket(name, date, priority, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                shouldCloseScreen.value = true
                Log.d("EditBasketViewModel", "updateBasket: basket with id $id")
            })
            { Log.e("EditBasketViewModel", "Error: updateBasket() ${it.printStackTrace()}") }
        compositeDisposable.add(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}