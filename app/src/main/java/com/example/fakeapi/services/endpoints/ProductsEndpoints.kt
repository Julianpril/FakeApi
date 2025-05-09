package com.example.fakeapi.services.endpoints

import com.example.fakeapi.services.models.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsEndpoints {
    @GET("products")
    suspend fun getAllProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @GET("products/slug/{slug}")
    suspend fun getProductBySlug(@Path("slug") slug: String): Product
}
