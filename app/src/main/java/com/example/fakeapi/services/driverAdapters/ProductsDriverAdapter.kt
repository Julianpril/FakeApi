package com.example.appretrofit.services.driverAdapters

import com.example.appretrofit.services.features.ProductsServices
import com.example.appretrofit.services.models.Product

class ProductsDriverAdapter {
    private val productsServices: ProductsServices = ProductsServices()

    fun loadProducts(
        loadData: (list: List<Product>) -> Unit,
        error: (msg: String) -> Unit
    ) {
        this.productsServices.getAllProducts(
            success = { loadData(it) },
            error = { error(it) }
        )
    }
}