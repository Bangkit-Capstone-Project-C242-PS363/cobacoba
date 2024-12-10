package com.adira.signmaster.data.response

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
data class BaseResponse(
    val error: Boolean,
    val message: String
)