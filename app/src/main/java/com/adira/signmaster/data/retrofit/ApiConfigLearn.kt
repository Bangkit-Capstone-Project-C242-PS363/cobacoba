package com.adira.signmaster.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfigLearn {
    private const val BASE_URL = "http://34.50.84.107/materials/"

    val instance: ApiServiceLearn by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceLearn::class.java)
    }
}




