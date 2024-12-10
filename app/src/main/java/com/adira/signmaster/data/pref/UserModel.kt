package com.adira.signmaster.data.pref

data class UserModel(
    val name: String,
    val token: String,
    val isLogin: Boolean = false,
    val email: String,
    val isSubscribed: Boolean = false
)
