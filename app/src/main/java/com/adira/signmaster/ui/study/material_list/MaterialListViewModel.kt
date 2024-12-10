package com.adira.signmaster.ui.study.material_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adira.signmaster.data.response.ChapterDetail
import com.adira.signmaster.data.retrofit.ApiConfigLearn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MaterialListViewModel : ViewModel() {

    private val _chapterDetails = MutableLiveData<ChapterDetail>()
    val chapterDetails: LiveData<ChapterDetail> get() = _chapterDetails

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchChapterDetails(chapterId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = ApiConfigLearn.instance.fetchChapterDetails(chapterId)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _chapterDetails.postValue(it)
                    } ?: run {
                        _errorMessage.postValue("No data found for this chapter.")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: HttpException) {
                _errorMessage.postValue("HTTP Error: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected Error: ${e.message}")
            } finally {
                _loading.value = false // Sembunyikan ProgressBar
            }
        }
    }
}



