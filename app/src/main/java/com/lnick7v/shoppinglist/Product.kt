package com.lnick7v.shoppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
class Product(
    @field: PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: Double
    ) {
    constructor(name: String, price: Double): this(0, name, price)
}