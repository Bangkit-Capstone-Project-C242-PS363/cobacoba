package com.adira.signmaster.data.retrofit

import com.adira.signmaster.data.response.MotionData
import com.adira.signmaster.data.response.ToMotionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceMotion {

    @POST("tomotion")
    fun sendMotionData(
        @Body data: MotionData
    ): Call<ToMotionResponse>

}
