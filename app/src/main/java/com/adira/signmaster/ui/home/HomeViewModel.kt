package com.adira.signmaster.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.adira.signmaster.data.repository.RepositoryAuth
import com.adira.signmaster.data.response.News
import com.adira.signmaster.data.retrofit.ApiConfigNews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val authRepository: RepositoryAuth,
) : ViewModel() {

    fun getUsername(): LiveData<String> {
        return authRepository.getUsername().asLiveData()
    }

    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> get() = _newsList

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var fetchInProgress = false

    fun getNews() {
        if (fetchInProgress) return
        _loading.value = true
        fetchInProgress = true

        val client = ApiConfigNews.apiServiceNews.getAllNews()
        client.enqueue(object : Callback<List<News>> {
            override fun onResponse(call: Call<List<News>>, response: Response<List<News>>) {
                _loading.value = false
                fetchInProgress = false

                if (response.isSuccessful && response.body() != null) {
                    _newsList.value = response.body()
                } else {
                    _error.value = "Failed to load news: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<List<News>>, t: Throwable) {
                _loading.value = false
                _error.value = "Error occurred: ${t.message}"
                fetchInProgress = false
                _error.value = "Error occurred: ${t.message}"
            }
        })
    }

    fun resetError() {
        _error.value = null
    }
}


