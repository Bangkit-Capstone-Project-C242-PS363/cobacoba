package com.adira.signmaster.ui.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adira.signmaster.data.model.Chapter
import com.adira.signmaster.data.model.Quiz
import com.adira.signmaster.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val repository = QuizRepository()

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> get() = _chapters

    private val _quiz = MutableLiveData<List<Quiz>>()
    val quiz: LiveData<List<Quiz>> get() = _quiz

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchChapters() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.fetchChapters()
                if (response?.error == false) {
                    _chapters.value = response.data
                } else {
                    _error.value = response?.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchQuiz(chapterId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.fetchQuiz(chapterId)
                if (response?.error == false) {
                    _quiz.value = response.data
                } else {
                    _error.value = response?.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("QuizViewModel", "Error fetching quizzes: ${e.message}")
            }
        }
    }
}
