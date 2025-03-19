package com.example.inventoryapptest.data.model

data class ItemResponse(
    val statusCode: Int,
    val message: String,
    val data: List<Item>
) 