package com.adira.signmaster.data.repository

import com.adira.signmaster.data.pref.UserModel
import com.adira.signmaster.data.pref.UserPreference
import com.adira.signmaster.data.response.LoginRequest
import com.adira.signmaster.data.response.LoginResponse
import com.adira.signmaster.data.response.RegisterRequest
import com.adira.signmaster.data.response.RegisterResponse
import com.adira.signmaster.data.retrofit.ApiServiceAuth
import kotlinx.coroutines.flow.Flow


class RepositoryAuth private constructor(
    private val apiService: ApiServiceAuth,
    private val pref: UserPreference
) {

    suspend fun register(username: String, email: String, password: String, confirmPassword: String): RegisterResponse {
        val registerRequest = RegisterRequest(username, email, password, confirmPassword)
        return apiService.register(registerRequest)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest)
    }

    fun getSession(): Flow<UserModel> {
        return pref.getLoginStatus()
    }


    fun getUsername(): Flow<String> {
        return pref.getUsername()
    }

    fun getEmail(): Flow<String> {
        return pref.getEmail()
    }

    suspend fun saveSession(user: UserModel) {
        pref.saveSession(user)
    }

    companion object {
        @Volatile
        private var instance: RepositoryAuth? = null
        fun getInstance(
            apiService: ApiServiceAuth,
            pref: UserPreference
        ): RepositoryAuth =
            instance ?: synchronized(this) {
                instance ?: RepositoryAuth(apiService, pref)
            }.also { instance = it }
    }
}


