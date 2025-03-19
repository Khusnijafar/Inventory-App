package com.example.inventoryapptest.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapptest.data.model.Item
import com.example.inventoryapptest.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadItemsFromApi(token: String) {
        Log.d("MainViewModel", "Starting to load items from API")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("MainViewModel", "Loading items from API with token: $token")
                val items = repository.getItemsFromApi(token)
                if (items != null) {
                    Log.d("MainViewModel", "Items loaded successfully, count: ${items.size}")
                    repository.refreshItems(items)
                    _items.value = items
                } else {
                    Log.e("MainViewModel", "Failed to load items from API")
                    _error.value = "Failed to load items"
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception while loading items", e)
                _error.value = e.message ?: "Failed to load items"
            } finally {
                _isLoading.value = false
                Log.d("MainViewModel", "Loading process completed")
            }
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.addItem(item)
                _items.value = _items.value + item
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add item"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateItem(item)
                _items.value = _items.value.map { 
                    if (it.id == item.id) item else it 
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update item"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteItem(item)
                _items.value = _items.value.filter { it.id != item.id }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete item"
            } finally {
                _isLoading.value = false
            }
        }
    }
}