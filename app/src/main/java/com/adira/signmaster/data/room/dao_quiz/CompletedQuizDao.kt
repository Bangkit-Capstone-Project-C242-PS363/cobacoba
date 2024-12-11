package com.adira.signmaster.data.room.dao_quiz

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adira.signmaster.data.room.CompletedQuiz
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedQuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedQuiz(completedQuiz: CompletedQuiz)

    @Query("SELECT * FROM completed_quiz")
    fun getAllCompletedQuizzes(): Flow<List<CompletedQuiz>>

    @Query("DELETE FROM completed_quiz WHERE id = :quizId")
    suspend fun deleteCompletedQuiz(quizId: Int)

    @Query("DELETE FROM completed_quiz")
    suspend fun deleteAllCompletedQuizzes()
}
