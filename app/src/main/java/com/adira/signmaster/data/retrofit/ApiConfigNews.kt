package com.adira.signmaster.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfigNews {
    private const val BASE_URL = "https://signmaster-service-news-kji5w4ybbq-et.a.run.app/"

    val apiServiceNews: ApiServiceNews by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceNews::class.java)
    }
}