package com.adira.signmaster.ui.quiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.adira.signmaster.R
import com.adira.signmaster.data.pref.UserPreference
import com.adira.signmaster.data.pref.dataStore
import com.adira.signmaster.databinding.ActivityQuizBinding
import com.adira.signmaster.ui.home.subscription.SubscriptionRequiredFragment
import com.adira.signmaster.ui.quiz.quiz_menu_fragment.QuizMenuFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private var _binding: ActivityQuizBinding? = null
    private val binding get() = _binding
    private val viewModel: QuizViewModel by viewModels()
    private var isVip: Boolean = false // VIP status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        binding?.ivBack?.setOnClickListener {
            onBackPressed()
        }

        fetchVipStatus()
        observeLoadingState()

    }

    private fun fetchVipStatus() {
        lifecycleScope.launch {
            val pref = UserPreference.getInstance(applicationContext.dataStore)
            isVip = pref.getSubscriptionStatus().first()

            setupRecyclerView()
            fetchChapters()
        }
    }

    private fun observeLoadingState() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding?.progressBar?.visibility = View.VISIBLE
                binding?.recyclerViewQuiz?.visibility = View.GONE
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.recyclerViewQuiz?.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = ChapterAdapter(isVip) { chapterId, chapterTitle, isLocked ->
            if (isLocked && !isVip) {
                showSubscriptionRequiredFragment()
            } else {
                navigateToQuizMenu(chapterId, chapterTitle)
            }
        }

        binding?.recyclerViewQuiz?.layoutManager = GridLayoutManager(this, 3)
        binding?.recyclerViewQuiz?.adapter = adapter

        viewModel.chapters.observe(this) { chapters ->
            adapter.submitList(chapters)
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchChapters() {
        lifecycleScope.launch {
            try {
                viewModel.fetchChapters()
            } catch (e: Exception) {
                Toast.makeText(this@QuizActivity, "Failed to fetch chapters", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToQuizMenu(chapterId: Int, chapterTitle: String) {
        val fragment = QuizMenuFragment.newInstance(chapterId, chapterTitle)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSubscriptionRequiredFragment() {
        val subscriptionFragment = SubscriptionRequiredFragment()
        subscriptionFragment.show(supportFragmentManager, "SubscriptionRequiredFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
