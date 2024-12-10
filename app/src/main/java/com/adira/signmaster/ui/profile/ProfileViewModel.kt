package com.adira.signmaster.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adira.signmaster.data.repository.RepositoryAuth

class ProfileViewModel(
    private val repository: RepositoryAuth
) : ViewModel() {

    fun getUsername(): LiveData<String> {
        return repository.getUsername().asLiveData()
    }

    fun getEmail(): LiveData<String> {
        return repository.getEmail().asLiveData()
    }

}