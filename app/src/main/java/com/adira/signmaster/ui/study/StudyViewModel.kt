package com.adira.signmaster.ui.study
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adira.signmaster.data.response.Chapter
import com.adira.signmaster.data.response.ChapterDetail
import com.adira.signmaster.data.response.ChapterListResponse
import com.adira.signmaster.data.retrofit.ApiConfigLearn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyViewModel : ViewModel() {

    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> get() = _chapters

    private val _chapterDetails = MutableLiveData<ChapterDetail>()
    val chapterDetails: LiveData<ChapterDetail> get() = _chapterDetails

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchChapters() {
        _isLoading.value = true
        ApiConfigLearn.instance.fetchChapters().enqueue(object : Callback<ChapterListResponse> {
            override fun onResponse(call: Call<ChapterListResponse>, response: Response<ChapterListResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _chapters.postValue(it)
                    } ?: run {
                        _errorMessage.postValue("No chapters available.")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ChapterListResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.postValue("Network Error: ${t.message}")
            }
        })
    }

}
