package com.adira.signmaster.ui.profile

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adira.signmaster.R
import com.adira.signmaster.data.pref.UserPreference
import com.adira.signmaster.data.pref.dataStore
import com.adira.signmaster.data.retrofit.ApiConfigAuth
import com.adira.signmaster.databinding.FragmentProfileBinding
import com.adira.signmaster.ui.login.LoginActivity
import com.adira.signmaster.ui.viewmodelfactory.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    private var isProcessingRequest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUsername().observe(viewLifecycleOwner) { username ->
            binding?.tvUsername?.text = username ?: getString(R.string.unknown_username)
        }

        profileViewModel.getEmail().observe(viewLifecycleOwner) { email ->
            binding?.tvEmail?.text = email ?: getString(R.string.unknown_email)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val pref = UserPreference.getInstance(requireContext().dataStore)
            pref.getSubscriptionStatus().collect { isSubscribed ->
                binding?.switchSubscription?.isChecked = isSubscribed
            }
        }

        binding?.switchSubscription?.setOnCheckedChangeListener { _, isChecked ->
            if (isProcessingRequest) return@setOnCheckedChangeListener
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                binding?.switchSubscription?.isChecked = !isChecked
                return@setOnCheckedChangeListener
            }
            handleSubscriptionChange(isChecked)
        }

        binding?.tvLogout?.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun handleSubscriptionChange(isSubscribed: Boolean) {
        binding?.switchSubscription?.isEnabled = false
        isProcessingRequest = true

        if (isSubscribed) {
            subscribe()
        } else {
            unsubscribe()
        }
    }

    private fun subscribe() {
        lifecycleScope.launch {
            val pref = UserPreference.getInstance(requireContext().dataStore)
            val token = pref.getLoginToken().firstOrNull() ?: run {
                Toast.makeText(requireContext(), "Unable to get login token", Toast.LENGTH_SHORT).show()
                resetSwitchState(false)
                return@launch
            }

            try {
                val response = ApiConfigAuth.getApiServiceAuth(token).subscribe()
                if (!response.error) {
                    pref.updateSubscriptionStatus(true)
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    resetSwitchState(false)
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Network error while subscribing", Toast.LENGTH_SHORT).show()
                resetSwitchState(false)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An unexpected error occurred", Toast.LENGTH_SHORT).show()
                resetSwitchState(false)
            } finally {
                isProcessingRequest = false
                binding?.switchSubscription?.isEnabled = true
            }
        }
    }

    private fun unsubscribe() {
        lifecycleScope.launch {
            val pref = UserPreference.getInstance(requireContext().dataStore)
            val token = pref.getLoginToken().firstOrNull() ?: run {
                Toast.makeText(requireContext(), "Unable to get login token", Toast.LENGTH_SHORT).show()
                resetSwitchState(true)
                return@launch
            }

            try {
                val response = ApiConfigAuth.getApiServiceAuth(token).unsubscribe()
                if (!response.error) {
                    pref.updateSubscriptionStatus(false)
                } else {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    resetSwitchState(true)
                }
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Network error while unsubscribing", Toast.LENGTH_SHORT).show()
                resetSwitchState(true)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An unexpected error occurred", Toast.LENGTH_SHORT).show()
                resetSwitchState(true)
            } finally {
                isProcessingRequest = false
                binding?.switchSubscription?.isEnabled = true
            }
        }
    }

    private fun resetSwitchState(isChecked: Boolean) {
        binding?.switchSubscription?.isChecked = isChecked
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_verification))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                logoutUser()
            }
            .show()
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            val pref = UserPreference.getInstance(requireContext().dataStore)
            pref.logout()
            Toast.makeText(requireContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
