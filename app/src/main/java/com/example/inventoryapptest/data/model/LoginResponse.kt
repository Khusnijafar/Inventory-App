package com.example.inventoryapptest.data.model

data class LoginResponse(
    val statusCode: Int,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val email: String,
    val name: String,
    val department: String,
    val position: String,
    val api_token: String
)