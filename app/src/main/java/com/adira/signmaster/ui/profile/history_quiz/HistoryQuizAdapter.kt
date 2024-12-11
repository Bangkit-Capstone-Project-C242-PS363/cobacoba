package com.adira.signmaster.ui.profile.history_quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.adira.signmaster.R
import com.adira.signmaster.data.room.CompletedQuiz
import com.bumptech.glide.Glide

class HistoryQuizAdapter(
    private val onDeleteClick: (Int) -> Unit // Callback to delete a specific quiz
) : ListAdapter<CompletedQuiz, HistoryQuizAdapter.HistoryViewHolder>(DiffCallback) {

    inner class HistoryViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val quizTitle: TextView = itemView.findViewById(R.id.tvQuizTitle)
        val quizIcon: ImageView = itemView.findViewById(R.id.ivQuizIcon)
        val deleteButton: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_history_quiz, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val quiz = getItem(position)
        holder.quizTitle.text = quiz.title
        Glide.with(holder.itemView.context)
            .load(quiz.iconUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_icon)
            .into(holder.quizIcon)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(quiz.id)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CompletedQuiz>() {
        override fun areItemsTheSame(oldItem: CompletedQuiz, newItem: CompletedQuiz): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CompletedQuiz, newItem: CompletedQuiz): Boolean {
            return oldItem == newItem
        }
    }
}
