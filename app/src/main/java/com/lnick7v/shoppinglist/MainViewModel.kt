package com.lnick7v.shoppinglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val basketsProductsDatabase = BasketsProductsDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    fun getBaskets(): LiveData<List<Basket>> {
        return basketsProductsDatabase.BasketDao().getBaskets()
    }

    fun remove(basket: Basket) {
        val disposable = basketsProductsDatabase.BasketDao().remove(basket.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("MainViewModel", "remove: basket with id ${basket.id}")
            })
            {
                Log.e("MainViewModel", "Error: remove() ${it.printStackTrace()}")
            }
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}