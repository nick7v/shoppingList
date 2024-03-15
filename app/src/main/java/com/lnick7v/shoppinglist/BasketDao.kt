package com.lnick7v.shoppinglist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface BasketDao {
    @Query("SELECT * FROM baskets")
    fun getBaskets(): LiveData<List<Basket>>

    /*@Query("SELECT COUNT(*) FROM baskets")
    fun getBasketsDBSize(): Int*/

    @Query("SELECT * FROM baskets WHERE id = :idBasket")
    //fun getOneBasket(idBasket: Int): Basket
    fun getOneBasket(idBasket: Long): Single<Basket>

    @Query("UPDATE baskets SET name = :name, date = :date, priority = :priority WHERE id = :id")
    fun updateBasket(name: String, date: String, priority: Int, id: Long): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(basket: Basket): Single <Long>

    @Query("DELETE FROM baskets WHERE id = :id")
    fun remove(id: Long): Completable
}