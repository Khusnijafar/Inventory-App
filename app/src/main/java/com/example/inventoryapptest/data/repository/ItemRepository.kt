package com.example.inventoryapptest.data.repository

import android.util.Log
import com.example.inventoryapptest.data.api.ApiService
import com.example.inventoryapptest.data.local.ItemDao
import com.example.inventoryapptest.data.model.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) {
    suspend fun getItemsFromApi(token: String): List<Item>? {
        Log.d("ItemRepository", "Starting to fetch items from API")
        return try {
            Log.d("ItemRepository", "Making API call with token: $token")
            val response = apiService.getItems("Bearer $token")
            Log.d("ItemRepository", "API response received: ${response.isSuccessful}, code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { itemResponse ->
                    Log.d("ItemRepository", "Response status code: ${itemResponse.statusCode}")
                    if (itemResponse.statusCode == 1) {
                        itemResponse.data.let { items ->
                            Log.d("ItemRepository", "Items received: ${items.size}")
                            try {
                                itemDao.insertItems(items)
                                Log.d("ItemRepository", "Items inserted into database successfully")
                                items
                            } catch (e: Exception) {
                                Log.e("ItemRepository", "Error inserting items into database", e)
                                null
                            }
                        }
                    } else {
                        Log.e("ItemRepository", "API returned error status code: ${itemResponse.statusCode}")
                        null
                    }
                }
            } else {
                Log.e("ItemRepository", "API call failed: ${response.code()}, ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Exception while fetching items from API", e)
            null
        }
    }

    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()

    suspend fun addItem(item: Item) {
        try {
            Log.d("ItemRepository", "Adding item: ${item.item_name}")
            itemDao.insertItem(item)
            Log.d("ItemRepository", "Item added successfully")
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error adding item", e)
            throw e
        }
    }

    suspend fun updateItem(item: Item) {
        try {
            Log.d("ItemRepository", "Updating item: ${item.item_name}")
            itemDao.updateItem(item)
            Log.d("ItemRepository", "Item updated successfully")
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error updating item", e)
            throw e
        }
    }

    suspend fun deleteItem(item: Item) {
        try {
            Log.d("ItemRepository", "Deleting item: ${item.item_name}")
            itemDao.deleteItem(item)
            Log.d("ItemRepository", "Item deleted successfully")
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error deleting item", e)
            throw e
        }
    }

    suspend fun refreshItems(items: List<Item>) {
        try {
            Log.d("ItemRepository", "Refreshing items, count: ${items.size}")
            itemDao.deleteAllItems()
            Log.d("ItemRepository", "All items deleted from database")
            itemDao.insertItems(items)
            Log.d("ItemRepository", "New items inserted into database")
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error refreshing items", e)
            throw e
        }
    }
}