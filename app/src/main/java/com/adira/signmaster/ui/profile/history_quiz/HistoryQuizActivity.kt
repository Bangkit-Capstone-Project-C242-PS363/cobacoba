package com.adira.signmaster.ui.profile.history_quiz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.adira.signmaster.data.room.database.QuizDatabase
import com.adira.signmaster.databinding.ActivityHistoryQuizBinding
import kotlinx.coroutines.launch

class HistoryQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryQuizBinding
    private lateinit var database: QuizDatabase
    private lateinit var adapter: HistoryQuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = QuizDatabase.getDatabase(this)

        supportActionBar?.title = "History"

        adapter = HistoryQuizAdapter { quizId ->
            lifecycleScope.launch {
                database.completedQuizDao().deleteCompletedQuiz(quizId) // Only delete a specific quiz
                Toast.makeText(this@HistoryQuizActivity, "Quiz Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        observeCompletedQuizzes()

        binding.btnClearHistory.setOnClickListener {
            lifecycleScope.launch {
                database.completedQuizDao().deleteAllCompletedQuizzes()
            }
        }
    }

    private fun observeCompletedQuizzes() {
        lifecycleScope.launch {
            database.completedQuizDao().getAllCompletedQuizzes().collect { quizzes ->
                adapter.submitList(quizzes)
            }
        }
    }
}
