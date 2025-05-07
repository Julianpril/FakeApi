package com.example.appretrofit.services.features

import androidx.lifecycle.viewModelScope
import com.example.appretrofit.services.endpoints.ProductsEndpoints
import com.example.appretrofit.services.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsServices: BaseService() {

    fun getAllProducts(
        success: (list:List<Product>) -> Unit,
        error: (data:String)->Unit
    ){
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val resp = getRetrofit().create(ProductsEndpoints::class.java)
                    .getAllProducts()
                when(val data = resp.body()){
                    null -> success(emptyList())
                    else -> success(data)
                }
            } catch (e: Exception){
                error("Error al consumir el servicio de productos")
            }
        }
    }
}