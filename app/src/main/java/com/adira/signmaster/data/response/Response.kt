package com.adira.signmaster.data.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult?
)

data class LoginResult(
    val userId: String,
    val name: String,
    val email: String,
    val token: String
)

data class RegisterResponse(
    val error: Boolean,
    val message: String
)
