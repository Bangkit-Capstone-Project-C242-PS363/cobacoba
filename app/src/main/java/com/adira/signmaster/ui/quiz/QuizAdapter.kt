package com.adira.signmaster.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adira.signmaster.R
import com.adira.signmaster.data.model.Chapter
import com.bumptech.glide.Glide

class ChapterAdapter(
    private val isVip: Boolean,
    private val onClick: (Int, String, Boolean) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private val chapters = mutableListOf<Chapter>()

    fun submitList(newChapters: List<Chapter>) {
        chapters.clear()
        chapters.addAll(newChapters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_quiz, parent, false)
        return ChapterViewHolder(view, onClick, isVip)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.bind(chapter)
    }

    override fun getItemCount() = chapters.size

    class ChapterViewHolder(
        itemView: View,
        private val onClick: (Int, String, Boolean) -> Unit,
        private val isVip: Boolean
    ) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.tvQuizTitle)
        private val icon: ImageView = itemView.findViewById(R.id.ivIconQuizCard)
        private val lockIcon: ImageView = itemView.findViewById(R.id.ivLockIcon)
        private val background: ImageView = itemView.findViewById(R.id.ivCardBackground)

        fun bind(chapter: Chapter) {
            title.text = chapter.title
            Glide.with(itemView.context)
                .load(chapter.icon_url)
                .placeholder(R.drawable.default_icon)
                .error(R.drawable.error_icon)
                .into(icon)

            val isLocked = chapter.locked && !isVip

            if (isLocked) {
                lockIcon.visibility = View.VISIBLE
                icon.alpha = 0.5f
                background.alpha = 0.5f
            } else {
                lockIcon.visibility = View.GONE
                icon.alpha = 1.0f
                background.alpha = 1.0f
            }

            itemView.setOnClickListener {
                val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_animation)
                itemView.startAnimation(animation)
                onClick(chapter.id, chapter.title, isLocked)
            }
        }
    }
}
