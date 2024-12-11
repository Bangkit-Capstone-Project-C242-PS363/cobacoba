package com.adira.signmaster.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_quiz")
data class CompletedQuiz(
    @PrimaryKey val id: Int,
    val title: String,
    val iconUrl: String
)
