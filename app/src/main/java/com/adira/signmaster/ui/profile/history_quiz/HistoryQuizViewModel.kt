package com.adira.signmaster.ui.profile.history_quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adira.signmaster.data.room.CompletedQuiz
import com.adira.signmaster.data.room.database.QuizDatabase
import kotlinx.coroutines.launch

class HistoryQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = QuizDatabase.getDatabase(application).completedQuizDao()

    val allCompletedQuizzes: LiveData<List<CompletedQuiz>> = dao.getAllCompletedQuizzes().asLiveData()

    fun deleteQuiz(quizId: Int) = viewModelScope.launch {
        dao.deleteCompletedQuiz(quizId)
    }

    fun clearAllHistory() = viewModelScope.launch {
        dao.deleteAllCompletedQuizzes()
    }
}
