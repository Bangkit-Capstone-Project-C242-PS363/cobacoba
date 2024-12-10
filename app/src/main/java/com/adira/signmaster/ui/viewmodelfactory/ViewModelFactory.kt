package com.adira.signmaster.ui.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adira.signmaster.data.di.InjectionAuth
import com.adira.signmaster.data.repository.RepositoryAuth
import com.adira.signmaster.ui.home.HomeViewModel
import com.adira.signmaster.ui.login.LoginViewModel
import com.adira.signmaster.ui.profile.ProfileViewModel
import com.adira.signmaster.ui.register.RegisterViewModel


class ViewModelFactory(
    private val repository: RepositoryAuth
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            RegisterViewModel::class.java -> RegisterViewModel(repository) as T
            HomeViewModel::class.java -> HomeViewModel(repository) as T
            ProfileViewModel::class.java -> ProfileViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    InjectionAuth.provideRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}

