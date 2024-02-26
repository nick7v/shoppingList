package com.lnick7v.shoppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baskets")
class Basket(
    @field: PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val date: String,
    val products: Product
) {
    constructor(name: String, date: String, products: Product): this(0, name, date, products)
}