package com.adira.signmaster.data.retrofit


import com.adira.signmaster.data.response.BaseResponse
import com.adira.signmaster.data.response.LoginRequest
import com.adira.signmaster.data.response.LoginResponse
import com.adira.signmaster.data.response.RegisterRequest
import com.adira.signmaster.data.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceAuth {

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @GET("auth/subscribe")
    suspend fun subscribe(): BaseResponse

    @GET("auth/unsubscribe")
    suspend fun unsubscribe(): BaseResponse
}