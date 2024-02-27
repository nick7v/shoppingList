package com.lnick7v.shoppinglist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AddBasketViewModel(application: Application): AndroidViewModel(application) {
    private val basketDao = BasketsProductsDatabase.getInstance(application).BasketDao()
    private var shouldCloseScreen = MutableLiveData<Boolean>()

    fun getCloseScreen(): LiveData<Boolean> = shouldCloseScreen

    fun addBasket(basket: Basket){
        Thread {
            basketDao.add(basket)
            shouldCloseScreen.postValue(true)
        }.start()
    }
}