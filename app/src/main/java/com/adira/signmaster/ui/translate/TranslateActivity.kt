package com.adira.signmaster.ui.translate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.adira.signmaster.R
import com.adira.signmaster.databinding.ActivityTranslateBinding
import com.adira.signmaster.ui.translate.camera_translate.CameraTranslateActivity

class TranslateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslateBinding
    private val translateViewModel: TranslateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        translateViewModel.videoUrl.observe(this, Observer { url ->
            displayVideo(url)
        })


        translateViewModel.errorMessage.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })


        translateViewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })


        binding.ivSend.setOnClickListener {
            val inputText = binding.textInputEditText.text.toString().trim()

            if (inputText.isNotEmpty()) {
                translateViewModel.sendToMotionApi(inputText)
            } else {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, CameraTranslateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayVideo(url: String) {
        val videoUri = Uri.parse(url)
        binding.vvSignLanguage.setVideoURI(videoUri)
        binding.vvSignLanguage.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }


        binding.vvSignLanguage.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) // Animasi balik
    }

}


