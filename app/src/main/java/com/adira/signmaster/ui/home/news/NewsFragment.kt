package com.adira.signmaster.ui.home.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.adira.signmaster.R
import com.adira.signmaster.databinding.FragmentNewsBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE

        val title = arguments?.getString("title")
        val description = arguments?.getString("description")
        val imageUrl = arguments?.getString("image")

        binding.apply {
            tvTitleNews.text = title
            tvDescriptionNews.text = description

            Glide.with(this@NewsFragment)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(imgNews)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        (activity as? AppCompatActivity)?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
    }

}

