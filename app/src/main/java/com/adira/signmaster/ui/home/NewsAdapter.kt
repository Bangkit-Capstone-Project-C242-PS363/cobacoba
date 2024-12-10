package com.adira.signmaster.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.adira.signmaster.R
import com.adira.signmaster.data.response.News
import com.adira.signmaster.databinding.CardNewsBinding
import com.bumptech.glide.Glide

class NewsAdapter(private val newsList: List<News>) :
    RecyclerView.Adapter<NewsAdapter.CardViewHolder>() {

    inner class CardViewHolder(private val binding: CardNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.tvEventName.text = news.title

            Glide.with(binding.root)
                .load(news.image)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.imgLogo)

            binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("title", news.title)
                    putString("description", news.description)
                    putString("image", news.image)
                }

                val navController = Navigation.findNavController(it)
                navController.navigate(R.id.action_homeFragment_to_newsFragment, bundle)
                (binding.root.context as? AppCompatActivity)?.overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size
}