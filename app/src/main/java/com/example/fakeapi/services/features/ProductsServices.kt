package com.example.fakeapi.services.features

import androidx.lifecycle.viewModelScope
import com.example.fakeapi.services.endpoints.ProductsEndpoints
import com.example.fakeapi.services.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit

class ProductsServices(private val retrofit: Retrofit) : BaseService() {

    private val productsEndpoints = retrofit.create(ProductsEndpoints::class.java)

    fun getAllProducts(
        success: (List<Product>) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val products = getRetrofit()
                    .create(ProductsEndpoints::class.java)
                    .getAllProducts()
                success(products)
            } catch (e: Exception) {
                error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun getProductById(
        id: Int,
        success: (Product) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val product = getRetrofit()
                    .create(ProductsEndpoints::class.java)
                    .getProductById(id)
                success(product)
            } catch (e: Exception) {
                error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun getProductBySlug(
        slug: String,
        success: (Product) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val product = getRetrofit()
                    .create(ProductsEndpoints::class.java)
                    .getProductBySlug(slug)
                success(product)
            } catch (e: Exception) {
                error("Network error: ${e.localizedMessage}")
            }
        }
    }
}

