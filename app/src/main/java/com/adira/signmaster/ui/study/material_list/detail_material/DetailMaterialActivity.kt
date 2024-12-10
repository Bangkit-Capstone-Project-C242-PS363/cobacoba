package com.adira.signmaster.ui.study.material_list.detail_material

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adira.signmaster.R
import com.adira.signmaster.data.response.MaterialDetail
import com.adira.signmaster.databinding.ActivityDetailMaterialBinding
import com.adira.signmaster.ui.study.material_list.MaterialListActivity
import com.bumptech.glide.Glide

class DetailMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMaterialBinding
    private var currentMaterialIndex = 0
    private lateinit var materials: List<MaterialDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val materialTitle = intent.getStringExtra("MATERIAL_TITLE")
        materials = intent.getParcelableArrayListExtra("MATERIALS_LIST") ?: emptyList()
        currentMaterialIndex = intent.getIntExtra("MATERIAL_INDEX", 0)

        binding.tvMaterialTitle.text = materialTitle
        updateVisualContent(materials[currentMaterialIndex])

        binding.fabMenu.setOnClickListener {
            val intent = Intent(this, MaterialListActivity::class.java)
            intent.putParcelableArrayListExtra("MATERIALS_LIST", ArrayList(materials))
            intent.putExtra("MATERIAL_TITLE", binding.tvMaterialTitle.text.toString())
            val chapterId = intent.getStringExtra("CHAPTER_ID")
            if (!chapterId.isNullOrEmpty()) {
                intent.putExtra("CHAPTER_ID", chapterId)
            } else {
                finish()
                return@setOnClickListener
            }

            startActivity(intent)
        }

        binding.fabNext.setOnClickListener {
            if (currentMaterialIndex < materials.size - 1) {
                currentMaterialIndex++
                val nextMaterial = materials[currentMaterialIndex]
                binding.tvMaterialTitle.text = nextMaterial.title
                updateVisualContent(nextMaterial)
            } else {
                Toast.makeText(this, "No more materials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabBefore.setOnClickListener {
            if (currentMaterialIndex > 0) {
                currentMaterialIndex--
                val previousMaterial = materials[currentMaterialIndex]
                binding.tvMaterialTitle.text = previousMaterial.title
                updateVisualContent(previousMaterial)
            } else {
                Toast.makeText(this, "No more previous materials", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateVisualContent(material: MaterialDetail) {
        val visualContentUrl = material.visual_content_url

        if (visualContentUrl.isNotEmpty()) {
            if (visualContentUrl.endsWith(".mp4")) {
                binding.vvMaterial.visibility = View.VISIBLE
                binding.ivMaterial.visibility = View.GONE

                val videoUri = Uri.parse(visualContentUrl)
                binding.vvMaterial.setVideoURI(videoUri)
                binding.vvMaterial.setOnPreparedListener { it.isLooping = true }
                binding.vvMaterial.start()
            } else {
                binding.vvMaterial.visibility = View.GONE
                binding.ivMaterial.visibility = View.VISIBLE

                Glide.with(this)
                    .load(visualContentUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_icon)
                    .into(binding.ivMaterial)
            }
        } else {
            Toast.makeText(this, "Visual content not available", Toast.LENGTH_SHORT).show()
            binding.vvMaterial.visibility = View.GONE
            binding.ivMaterial.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}







