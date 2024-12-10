package com.adira.signmaster.ui.translate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adira.signmaster.data.response.MotionData
import com.adira.signmaster.data.response.ToMotionResponse
import com.adira.signmaster.data.retrofit.ApiConfigMotion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateViewModel : ViewModel() {
    val videoUrl = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun sendToMotionApi(text: String) {
        isLoading.value = true

        val apiService = ApiConfigMotion.getApiService()
        apiService.sendMotionData(MotionData(data = text)).enqueue(object : Callback<ToMotionResponse> {
            override fun onResponse(call: Call<ToMotionResponse>, response: Response<ToMotionResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && !result.error) {
                        videoUrl.value = result.url
                    } else {
                        errorMessage.value = result?.message ?: "Unknown error"
                    }
                } else {
                    errorMessage.value = "Request failed"
                }
            }

            override fun onFailure(call: Call<ToMotionResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "API call failed: ${t.message}"
            }
        })
    }
}

