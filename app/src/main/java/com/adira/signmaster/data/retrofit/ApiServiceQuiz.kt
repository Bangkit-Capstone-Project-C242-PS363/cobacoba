package com.adira.signmaster.data.retrofit

import com.adira.signmaster.data.model.ChapterResponse
import com.adira.signmaster.data.model.QuizResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceQuiz {
    @GET("getchapters")
    suspend fun getChapters(): Response<ChapterResponse>

    @GET("getquizz/{chapterId}")
    suspend fun getQuiz(@Path("chapterId") chapterId: Int): Response<QuizResponse>
}
