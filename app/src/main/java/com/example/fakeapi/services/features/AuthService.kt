package com.example.fakeapi.services.features

import androidx.lifecycle.viewModelScope
import com.example.fakeapi.services.models.AuthResponse
import com.example.fakeapi.services.models.UserCredentials
import com.example.fakeapi.services.models.UserRegister
import com.example.fakeapi.services.endpoints.AuthEndpoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthService : BaseService() {

    fun login(
        credentials: UserCredentials,
        success: (response: AuthResponse) -> Unit,
        error: (msg: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = getRetrofit().create(AuthEndpoints::class.java)
                    .login(credentials)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        success(authResponse)
                    } else {
                        error("Authentication failed: Empty response")
                    }
                } else {
                    error("Authentication failed: ${response.message()}")
                }
            } catch (e: Exception) {
                error("Error during authentication: ${e.message}")
            }
        }
    }

    fun register(
        user: UserRegister,
        success: (response: AuthResponse) -> Unit,
        error: (msg: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = getRetrofit().create(AuthEndpoints::class.java)
                    .register(user)

                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        success(authResponse)
                    } else {
                        error("Registration failed: Empty response")
                    }
                } else {
                    error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                error("Error during registration: ${e.message}")
            }
        }
    }
}