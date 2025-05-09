package com.example.fakeapi.services.endpoints

import com.example.fakeapi.services.models.AuthResponse
import com.example.fakeapi.services.models.UserCredentials
import com.example.fakeapi.services.models.UserRegister
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthEndpoints {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body credentials: UserCredentials): Response<AuthResponse>

    @POST("/api/v1/users/")
    suspend fun register(@Body user: UserRegister): Response<AuthResponse>
}
