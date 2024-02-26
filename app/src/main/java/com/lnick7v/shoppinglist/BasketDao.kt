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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(basket: Basket)

    @Query("DELETE FROM baskets WHERE id = :id")
    fun remove(id: Int)
}