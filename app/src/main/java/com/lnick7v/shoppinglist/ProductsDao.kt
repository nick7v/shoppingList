package com.lnick7v.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products WHERE basketId = :idBasket")
    fun getProducts(idBasket: Int): LiveData<List<Product>> // get all products from Product DB
    //fun getProducts(idBasket: Int): List<Product> // get all products from Product DB

    @Insert(onConflict = OnConflictStrategy.REPLACE) // add product to Product DB
    fun add(product: Product)

    @Query("UPDATE products SET name = :name WHERE id = :id")
    fun updateProduct(name: String, id: Int)

    @Update
    fun updateProductFields(product: Product)

    @Query("DELETE FROM products WHERE id = :id")  // remove product from Product DB
    fun remove(id: Int)
}