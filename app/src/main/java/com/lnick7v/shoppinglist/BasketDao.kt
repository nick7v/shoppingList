package com.lnick7v.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BasketDao {
    @Query("SELECT * FROM baskets")
    fun getBaskets(): LiveData<List<Basket>>

    /*@Query("SELECT COUNT(*) FROM baskets")
    fun getBasketsDBSize(): Int*/

    @Query("SELECT * FROM baskets WHERE id = :idBasket")
    fun getOneBasket(idBasket: Int): Basket

    @Query("UPDATE baskets SET name = :name, date = :date, priority = :priority WHERE id = :id")
    fun updateBasket(name: String, date: String, priority: Int, id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(basket: Basket): Long

    @Query("DELETE FROM baskets WHERE id = :id")
    fun remove(id: Int)
}