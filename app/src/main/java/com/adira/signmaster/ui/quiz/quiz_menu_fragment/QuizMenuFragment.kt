package com.adira.signmaster.ui.quiz.quiz_menu_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adira.signmaster.R
import com.adira.signmaster.ui.quiz.quiz_material.QuizMaterialActivity
import com.adira.signmaster.ui.study.StudyActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuizMenuFragment : Fragment() {
    private var chapterId: Int? = null
    private var chapterTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chapterId = arguments?.getInt(ARG_CHAPTER_ID)
        chapterTitle = arguments?.getString(ARG_CHAPTER_TITLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_quiz_menu, container, false)
        view.findViewById<Button>(R.id.btnStartQuiz).setOnClickListener {
            if (chapterId != null && chapterTitle != null) {
                startQuizActivity(chapterId!!, chapterTitle!!)
            } else {
                showError("Chapter data is null")
            }
        }
        view.findViewById<Button>(R.id.btnLearn).setOnClickListener {
            startStudyActivity()
        }

        view.findViewById<FloatingActionButton>(R.id.fabMenu).setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }



    private fun startQuizActivity(chapterId: Int, chapterTitle: String) {
        val intent = Intent(requireContext(), QuizMaterialActivity::class.java).apply {
            putExtra(QuizMaterialActivity.EXTRA_CHAPTER_ID, chapterId)
            putExtra(QuizMaterialActivity.EXTRA_CHAPTER_TITLE, chapterTitle)
        }
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun startStudyActivity() {
        val intent = Intent(requireContext(), StudyActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_CHAPTER_ID = "chapter_id"
        private const val ARG_CHAPTER_TITLE = "chapter_title"

        fun newInstance(chapterId: Int, chapterTitle: String): QuizMenuFragment {
            return QuizMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CHAPTER_ID, chapterId)
                    putString(ARG_CHAPTER_TITLE, chapterTitle)
                }
            }
        }
    }

}
