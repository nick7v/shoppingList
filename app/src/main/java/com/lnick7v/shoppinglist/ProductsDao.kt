package com.lnick7v.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products")
    fun getProducts(): LiveData<List<Product>> // get all products from Product DB

    @Insert(onConflict = OnConflictStrategy.REPLACE) // add product to Product DB
    fun add(product: Product)

    @Query("DELETE FROM products WHERE id = :id")  // remove product from Product DB
    fun remove(id: Int)
}