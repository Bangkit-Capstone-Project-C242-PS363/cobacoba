package com.adira.signmaster.data.di

import android.content.Context
import com.adira.signmaster.data.pref.UserPreference
import com.adira.signmaster.data.pref.dataStore
import com.adira.signmaster.data.repository.RepositoryAuth
import com.adira.signmaster.data.retrofit.ApiConfigAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object InjectionAuth {
    fun provideRepository(context: Context): RepositoryAuth {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getLoginStatus().first()
        }
        val apiService = ApiConfigAuth.getApiServiceAuth(user.token)
        return RepositoryAuth.getInstance(apiService, pref)
    }
}


