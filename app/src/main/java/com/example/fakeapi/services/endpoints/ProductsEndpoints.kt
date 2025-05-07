package com.example.appretrofit.services.endpoints

import com.example.appretrofit.services.models.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductsEndpoints {

    @GET("/products")
    suspend fun getAllProducts(): Response<List<Product>>

}