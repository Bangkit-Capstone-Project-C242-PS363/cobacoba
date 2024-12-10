package com.adira.signmaster.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adira.signmaster.data.response.Chapter
import com.adira.signmaster.databinding.CardLearnMaterialBinding
import com.bumptech.glide.Glide

class LearnMaterialAdapter(
    private val isVip: Boolean, // Pass VIP status from StudyActivity
    private val onItemClick: (Chapter) -> Unit
) : ListAdapter<Chapter, LearnMaterialAdapter.LearnMaterialViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnMaterialViewHolder {
        val binding = CardLearnMaterialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LearnMaterialViewHolder(binding, isVip, onItemClick)
    }

    override fun onBindViewHolder(holder: LearnMaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LearnMaterialViewHolder(
        private val binding: CardLearnMaterialBinding,
        private val isVip: Boolean,
        private val onItemClick: (Chapter) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chapter: Chapter) {
            binding.tvMaterialTitle.text = chapter.title
            Glide.with(binding.iconStudyCard.context)
                .load(chapter.icon_url)
                .into(binding.iconStudyCard)

            // Determine lock state dynamically
            val isLocked = chapter.locked && !isVip

            if (isLocked) {
                binding.iconLock.visibility = View.VISIBLE
                binding.iconStudyCard.alpha = 0.5f // Dim the card icon
                binding.circleStroke.alpha = 0.5f // Dim the background circle
            } else {
                binding.iconLock.visibility = View.GONE
                binding.iconStudyCard.alpha = 1.0f // Reset brightness
                binding.circleStroke.alpha = 1.0f
            }

            // Handle click events
            binding.root.setOnClickListener {
                onItemClick(chapter)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter) = oldItem == newItem
    }
}
