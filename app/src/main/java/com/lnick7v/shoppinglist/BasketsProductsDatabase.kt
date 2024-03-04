package com.lnick7v.shoppinglist

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DB_NAME = "baskets_products.db"
@Database (entities = [Basket::class, Product::class], version = 6, exportSchema = false)
abstract class BasketsProductsDatabase: RoomDatabase() {
    companion object {
        private var instance: BasketsProductsDatabase? = null
        fun getInstance(application: Application): BasketsProductsDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(application, BasketsProductsDatabase::class.java, DB_NAME )
                    .fallbackToDestructiveMigration()  // при изменении версии БД - удалять данные
                    .build()
            }
            return instance!!
        }
    }
   abstract fun ProductsDao(): ProductsDao
    abstract fun BasketDao(): BasketDao
}