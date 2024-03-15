package com.lnick7v.shoppinglist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Basket::class,
        parentColumns = ["id"],   //id Basket
        childColumns = ["basketId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Product(
    @field: PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: Double,
    val basketId: Long
) {
    @Ignore
    constructor(name: String, price: Double, basketId: Long) : this(0, name, price, basketId)
}