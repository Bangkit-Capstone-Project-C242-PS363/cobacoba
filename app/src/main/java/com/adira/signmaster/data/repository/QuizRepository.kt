package com.adira.signmaster.data.repository

import android.util.Log
import com.adira.signmaster.data.model.ChapterResponse
import com.adira.signmaster.data.model.QuizResponse
import com.adira.signmaster.data.retrofit.ApiConfigQuiz

class QuizRepository {

    private val api = ApiConfigQuiz.retrofit

    suspend fun fetchChapters(): ChapterResponse? {
        val response = api.getChapters()
        if (response.isSuccessful) {
            Log.d("QuizRepository", "Chapters fetched successfully: ${response.body()}")
            return response.body()
        } else {
            Log.e("QuizRepository", "Failed to fetch chapters: ${response.errorBody()?.string()}")
            return null
        }
    }

    suspend fun fetchQuiz(chapterId: Int): QuizResponse? {
        val response = api.getQuiz(chapterId)
        return if (response.isSuccessful) {
            response.body()?.also {
                Log.d("QuizRepository", "Quiz fetched successfully: ${it.data}")
            }
        } else {
            Log.e("QuizRepository", "Failed to fetch quiz: ${response.errorBody()?.string()}")
            null
        }
    }
}
