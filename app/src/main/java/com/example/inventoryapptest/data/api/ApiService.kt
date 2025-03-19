package com.example.inventoryapptest.data.api

import com.example.inventoryapptest.data.model.LoginResponse
import com.example.inventoryapptest.data.model.ItemResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("list-items")
    suspend fun getItems(
        @Header("Authorization") token: String
    ): Response<ItemResponse>
}