package com.example.appretrofit.services.features

import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseService: ViewModel() {
    private val domainUrl:String = "https://fakestoreapi.com"

    public fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(this.domainUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}