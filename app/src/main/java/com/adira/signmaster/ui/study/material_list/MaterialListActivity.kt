package com.adira.signmaster.ui.study.material_list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adira.signmaster.R
import com.adira.signmaster.databinding.ActivityMaterialListBinding
import com.adira.signmaster.ui.quiz.quiz_material.QuizMaterialActivity.Companion.EXTRA_CHAPTER_TITLE
import com.adira.signmaster.ui.study.material_list.detail_material.DetailMaterialActivity
import com.bumptech.glide.Glide

class MaterialListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialListBinding
    private lateinit var adapter: MaterialListAdapter
    private val viewModel: MaterialListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toolbarMaterial.setNavigationOnClickListener {
            onBackPressed()
        }

        val chapterTitle = intent.getStringExtra("CHAPTER_TITLE")
        binding.materialTitle.text = chapterTitle ?: "Unknown Chapter"

        adapter = MaterialListAdapter { material ->
            val position = adapter.currentList.indexOf(material)
            val intent = Intent(this, DetailMaterialActivity::class.java).apply {
                putExtra("MATERIAL_ID", material.id)
                putExtra("MATERIAL_TITLE", material.title)
                putExtra("VISUAL_CONTENT_URL", material.visual_content_url)
                putParcelableArrayListExtra("MATERIALS_LIST", ArrayList(adapter.currentList))
                putExtra("MATERIAL_INDEX", position)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.rvMaterialList.layoutManager = LinearLayoutManager(this)
        binding.rvMaterialList.adapter = adapter

        val chapterId = intent.getStringExtra("CHAPTER_ID")
        if (!chapterId.isNullOrEmpty()) {
            viewModel.fetchChapterDetails(chapterId)
        } else {
            Toast.makeText(this, "Invalid Chapter ID", Toast.LENGTH_SHORT).show()
            finish()
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.chapterDetails.observe(this, Observer { chapterDetail ->
            Glide.with(this)
                .load(chapterDetail.chapterImageUrl)
                .into(binding.ivMaterialList)
            adapter.submitList(chapterDetail.materials)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}






