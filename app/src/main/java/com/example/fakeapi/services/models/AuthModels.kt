package com.example.fakeapi.services.models

data class UserCredentials(
    val email: String,
    val password: String
)

data class UserRegister(
    val name: String,
    val email: String,
    val password: String,
    val avatar: String
)


data class AuthResponse(
    val token: String,
    val id: Int,
    val username: String,
    val email: String
)