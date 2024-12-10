package com.adira.signmaster.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adira.signmaster.data.pref.UserModel
import com.adira.signmaster.data.repository.RepositoryAuth
import com.adira.signmaster.data.response.ErrorResponse
import com.adira.signmaster.data.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.adira.signmaster.data.repository.Result

class LoginViewModel(
    private val repository: RepositoryAuth
) : ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val isError: LiveData<String?> = _errorMessage

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val loginResponse = repository.login(email, password)
                when {
                    loginResponse.loginResult != null -> {
                        saveSession(
                            UserModel(
                                loginResponse.loginResult.name,
                                loginResponse.loginResult.token,
                                true,
                                loginResponse.loginResult.email,
                            )
                        )
                        _isSuccess.value = true
                        _loginResult.value = Result.Success(loginResponse)
                        _errorMessage.value = null
                    }

                    else -> {
                        throw Exception(loginResponse.message)
                    }
                }
            } catch (e: HttpException) {
                val errorBody = Gson().fromJson(e.response()?.errorBody()?.string(), ErrorResponse::class.java)
                val errorMessage = errorBody?.message ?: e.message()
                _errorMessage.value = errorMessage.toString()
                _isSuccess.value = false
                _loginResult.value = Result.Error(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
}