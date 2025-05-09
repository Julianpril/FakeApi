package com.example.fakeapi.services.features

import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseService : ViewModel() {
    private val domainUrl: String = "https://api.escuelajs.co/api/v1/"

    fun getRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(domainUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
