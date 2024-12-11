package com.adira.signmaster.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfigQuiz {
    private const val BASE_URL = "https://signmaster-material-quiz-kji5w4ybbq-et.a.run.app/quiz/"

    val retrofit: ApiServiceQuiz by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceQuiz::class.java)
    }
}
