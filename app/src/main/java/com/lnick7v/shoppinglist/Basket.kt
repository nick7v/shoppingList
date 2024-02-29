package com.lnick7v.shoppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baskets")
class Basket(
    @field: PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val priority: Int,
    val date: String
) {
    //constructor(name: String, priority: Int, date: String): this(0, name, priority, date)
}