package com.lnick7v.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products WHERE basketId = :idBasket")
    fun getProducts(idBasket: Long): LiveData<List<Product>> // get all products from Product DB

    @Insert(onConflict = OnConflictStrategy.REPLACE) // add product to Product DB
    fun add(product: Product): Completable

    @Query("UPDATE products SET name = :name WHERE id = :id")
    fun updateProduct(name: String, id: Int): Completable

    @Query("DELETE FROM products WHERE id = :id")  // remove product from Product DB
    fun remove(id: Int): Completable
}