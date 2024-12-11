package com.adira.signmaster.ui.quiz.quiz_material

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adira.signmaster.R
import com.adira.signmaster.data.model.Quiz
import com.adira.signmaster.data.room.CompletedQuiz
import com.adira.signmaster.data.room.database.QuizDatabase
import com.adira.signmaster.databinding.ActivityQuizMaterialBinding
import com.adira.signmaster.ui.quiz.QuizViewModel
import com.adira.signmaster.ui.quiz.quiz_result.QuizResultFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.launch

class QuizMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizMaterialBinding
    private val viewModel: QuizViewModel by viewModels()
    private var quizList: List<Quiz> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0
    private lateinit var database: QuizDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = QuizDatabase.getDatabase(this)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val chapterId = intent.getIntExtra(EXTRA_CHAPTER_ID, -1)
        val chapterTitle = intent.getStringExtra(EXTRA_CHAPTER_TITLE)

        if (chapterId == -1 || chapterTitle.isNullOrEmpty()) {
            showErrorAndExit("Invalid chapter data")
            return
        }

        supportActionBar?.hide()

        binding.toolbarTitle.text = chapterTitle

        setupUI()

        fetchQuizzes(chapterId)
    }

    private fun setupUI() {
        binding.progressBar.progress = 0
        binding.fabRepeatQuiz.setOnClickListener {
            restartQuiz()
        }
    }

    private fun fetchQuizzes(chapterId: Int) {
        viewModel.fetchQuiz(chapterId)
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.quiz.observe(this) { quizzes ->
            if (!quizzes.isNullOrEmpty()) {
                quizList = quizzes
                if (quizList.all { it.answers.isNotEmpty() }) {
                    displayQuiz(currentQuestionIndex)
                } else {
                    showErrorAndExit("Some questions have no answers")
                }
            } else {
                showErrorAndExit("No quizzes available for this chapter")
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showErrorAndExit("Error fetching quizzes: $errorMessage")
            }
        }
    }

    private fun displayQuiz(index: Int) {
        if (index >= quizList.size) {
            showQuizResult()
            return
        }

        val question = quizList[index]

        val progress = ((index + 1) * 100) / quizList.size
        binding.progressBar.progress = progress

        preloadNextQuestionImage(index)

        updateVisualContent(question.question, binding.ivQuiz, binding.vvQuiz)

        setupAnswerButtons(question)
    }

    private fun preloadNextQuestionImage(index: Int) {
        if (index + 1 < quizList.size) {
            Glide.with(this)
                .load(quizList[index + 1].question)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload()
        }
    }

    private fun updateVisualContent(contentUrl: String?, imageView: ImageView, videoView: VideoView) {
        if (!contentUrl.isNullOrEmpty()) {
            if (contentUrl.endsWith(".mp4")) {
                videoView.visibility = View.VISIBLE
                imageView.visibility = View.GONE

                val videoUri = Uri.parse(contentUrl)
                videoView.setVideoURI(videoUri)
                videoView.setOnPreparedListener { it.isLooping = true }
                videoView.start()
            } else {
                videoView.visibility = View.GONE
                imageView.visibility = View.VISIBLE

                Glide.with(imageView.context)
                    .load(contentUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_icon)
                    .into(imageView)
            }
        } else {
            Toast.makeText(imageView.context, "Konten tidak tersedia", Toast.LENGTH_SHORT).show()
            videoView.visibility = View.GONE
            imageView.visibility = View.GONE
        }
    }

    private fun setupAnswerButtons(question: Quiz) {
        val buttons = listOf(binding.btn1, binding.btn2, binding.btn3, binding.btn4)

        buttons.forEach {
            it.visibility = View.GONE
            it.text = ""
        }
        question.answers.forEachIndexed { index, answer ->
            if (index < buttons.size) {
                buttons[index].apply {
                    text = answer.answer
                    visibility = View.VISIBLE
                    val animation = android.view.animation.AnimationUtils.loadAnimation(this@QuizMaterialActivity, R.anim.slide_up)
                    this.startAnimation(animation)

                    setOnClickListener {
                        validateAnswer(index, question.correctAnswerIndex)
                    }
                }
            }
        }
    }

    private fun validateAnswer(selectedIndex: Int, correctIndex: Int) {
        if (selectedIndex == correctIndex) {
            correctAnswersCount++
        }

        currentQuestionIndex++
        displayQuiz(currentQuestionIndex)
    }

    private fun restartQuiz() {
        currentQuestionIndex = 0
        correctAnswersCount = 0

        binding.ivQuiz.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.btn1.visibility = View.VISIBLE
        binding.btn2.visibility = View.VISIBLE
        binding.btn3.visibility = View.VISIBLE
        binding.btn4.visibility = View.VISIBLE
        binding.fabRepeatQuiz.visibility = View.VISIBLE
        buttons().forEach { button ->
            button.setBackgroundColor(getColor(R.color.white))
        }

        displayQuiz(currentQuestionIndex)
    }

    private fun buttons() = listOf(binding.btn1, binding.btn2, binding.btn3, binding.btn4)

    private fun showQuizResult() {
        binding.ivQuiz.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.btn1.visibility = View.GONE
        binding.btn2.visibility = View.GONE
        binding.btn3.visibility = View.GONE
        binding.btn4.visibility = View.GONE
        binding.fabRepeatQuiz.visibility = View.GONE

        val iconUrl = intent.getStringExtra(EXTRA_ICON_URL) ?: ""
        val chapterId = intent.getIntExtra(EXTRA_CHAPTER_ID, -1)
        val chapterTitle = intent.getStringExtra(EXTRA_CHAPTER_TITLE) ?: "Unknown"

        lifecycleScope.launch {
            val completedQuiz = CompletedQuiz(
                id = chapterId,
                title = chapterTitle,
                iconUrl = iconUrl
            )
            database.completedQuizDao().insertCompletedQuiz(completedQuiz)
        }

        val fragment = QuizResultFragment.newInstance(quizList, correctAnswersCount)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showErrorAndExit(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("QuizMaterialActivity", message)
        finish()
    }

    companion object {
        const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        const val EXTRA_CHAPTER_TITLE = "extra_chapter_title"
        const val EXTRA_ICON_URL = "extra_icon_url"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
