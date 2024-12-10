package com.adira.signmaster.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adira.signmaster.data.repository.RepositoryAuth
import kotlinx.coroutines.launch
import com.adira.signmaster.data.repository.Result

class RegisterViewModel(
    private val repositoryAuth: RepositoryAuth) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _isError = MutableLiveData<String?>()
    val isError: LiveData<String?> = _isError

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                val response = repositoryAuth.register(name, email, password, confirmPassword)

                if (response.error) {
                    _registerResult.value = Result.Error(response.message)
                } else {
                    _registerResult.value = Result.Success(response.message)
                }
            } catch (e: Exception) {
                _isError.value = "Error: ${e.message}"
                _registerResult.value = Result.Error("Registration failed due to an error: ${e.message}")
            }
        }
    }
}
