package com.example.inventoryapptest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    val id: Int,
    val item_name: String,
    val stock: Int,
    val unit: String
)