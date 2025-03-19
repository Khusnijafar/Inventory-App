package com.example.inventoryapptest.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inventoryapptest.data.api.ApiService
import com.example.inventoryapptest.data.api.RetrofitClient
import com.example.inventoryapptest.data.local.AppDatabase
import com.example.inventoryapptest.data.repository.ItemRepository

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val apiService = RetrofitClient.apiService
            val database = AppDatabase.getDatabase(context)
            val repository = ItemRepository(apiService, database.itemDao())
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 