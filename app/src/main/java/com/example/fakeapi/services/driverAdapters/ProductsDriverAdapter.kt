package com.exam.fakeapi.services.driverAdapters

import com.example.fakeapi.services.endpoints.ProductsEndpoints
import com.example.fakeapi.services.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsDriverAdapter {

    private val api: ProductsEndpoints by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.escuelajs.co/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductsEndpoints::class.java)
    }

    fun loadProducts(
        loadData: (List<Product>) -> Unit,
        error: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = api.getAllProducts()
                loadData(products)
            } catch (e: Exception) {
                error(e.message ?: "Unknown error")
            }
        }
    }
}
