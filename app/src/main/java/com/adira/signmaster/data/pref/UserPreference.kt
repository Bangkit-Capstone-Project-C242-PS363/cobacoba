package com.adira.signmaster.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME] ?: ""
        }
    }

    fun getEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL] ?: ""
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[EMAIL] = user.email
            preferences[SUBSCRIPTION_STATUS_KEY] = user.isSubscribed
        }
    }

    suspend fun updateSubscriptionStatus(isSubscribed: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUBSCRIPTION_STATUS_KEY] = isSubscribed
        }
    }

    fun getSubscriptionStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SUBSCRIPTION_STATUS_KEY] ?: false
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getLoginStatus(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                name = preferences[USERNAME] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] == true,
                email = preferences[EMAIL] ?: "",
                isSubscribed = preferences[SUBSCRIPTION_STATUS_KEY] ?: false
            )
        }
    }

    fun getLoginToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val EMAIL = stringPreferencesKey("email")
        private val SUBSCRIPTION_STATUS_KEY = booleanPreferencesKey("subscription_status")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
