package com.adira.signmaster.data.retrofit

import com.adira.signmaster.data.response.ChapterDetailResponse
import com.adira.signmaster.data.response.ChapterListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceLearn {

    @GET("getchapters")
    fun fetchChapters(): Call<ChapterListResponse>

    @GET("getmaterials/{chapterId}")
    suspend fun fetchChapterDetails(
        @Path("chapterId") chapterId: String
    ): Response<ChapterDetailResponse>

}