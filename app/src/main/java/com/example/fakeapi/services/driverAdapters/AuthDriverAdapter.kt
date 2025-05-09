package com.example.fakeapi.services.driverAdapters

import com.example.fakeapi.services.features.AuthService
import com.example.fakeapi.services.models.AuthResponse
import com.example.fakeapi.services.models.UserCredentials
import com.example.fakeapi.services.models.UserRegister

class AuthDriverAdapter {
    private val authService: AuthService = AuthService()

    fun login(
        credentials: UserCredentials,
        onSuccess: (response: AuthResponse) -> Unit,
        onError: (msg: String) -> Unit
    ) {
        authService.login(
            credentials = credentials,
            success = { onSuccess(it) },
            error = { onError(it) }
        )
    }

    fun register(
        user: UserRegister,
        onSuccess: (response: AuthResponse) -> Unit,
        onError: (msg: String) -> Unit
    ) {
        authService.register(
            user = user,
            success = { onSuccess(it) },
            error = { onError(it) }
        )
    }
}