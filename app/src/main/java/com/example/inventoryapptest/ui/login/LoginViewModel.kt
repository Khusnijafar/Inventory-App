package com.example.inventoryapptest.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapptest.data.api.ApiService
import com.example.inventoryapptest.data.model.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val apiService: ApiService) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        Log.d("LoginViewModel", "Starting login process for email: $email")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Making API call to login endpoint")
                val response = apiService.login(email, password)
                Log.d("LoginViewModel", "API response received: ${response.isSuccessful}, code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    Log.d("LoginViewModel", "Login successful, status code: ${response.body()?.statusCode}")
                    _loginResult.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Login failed. Response code: ${response.code()}, Error body: $errorBody")
                    _error.value = "Login failed: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during login", e)
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}