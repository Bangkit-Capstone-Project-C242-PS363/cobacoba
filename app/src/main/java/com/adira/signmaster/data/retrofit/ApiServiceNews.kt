package com.adira.signmaster.data.retrofit

import com.adira.signmaster.data.response.News
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceNews {

    @GET("news")
    fun getAllNews(): Call<List<News>>

}