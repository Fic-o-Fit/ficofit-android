package com.c22ps072.ficofit.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_REFRESH_TOKEN = stringPreferencesKey("user_refresh_token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_PASSWORD_KEY = stringPreferencesKey("user_password_key")

    fun getUserToken(): Flow<String> = dataStore.data.map { it[USER_TOKEN_KEY] ?: "" }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    fun getUserRefreshToken(): Flow<String> = dataStore.data.map { it[USER_REFRESH_TOKEN] ?: "" }

    suspend fun saveUserRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_REFRESH_TOKEN] = token
        }
    }

    fun getUserName(): Flow<String> = dataStore.data.map { it[USER_NAME_KEY] ?: "not_set_yet" }



    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    fun getUserEmail(): Flow<String> = dataStore.data.map { it[USER_EMAIL_KEY] ?: "" }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    fun getUserPassword(): Flow<String> = dataStore.data.map { it[USER_PASSWORD_KEY] ?: "" }

    suspend fun saveUserPassword(password: String) {
        dataStore.edit { preferences ->
            preferences[USER_PASSWORD_KEY] = password
        }
    }

    fun getUserCaloriesBurn() : Flow<Double> = dataStore.data.map { it[USER_CALORIES_BURN] ?: 0.0 }

    suspend fun saveUserCaloriesBurn(calories: Double) {
        dataStore.edit { preferences ->
            preferences[USER_CALORIES_BURN] = calories
        }
    }

    suspend fun clearCache() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        val USER_CALORIES_BURN = doublePreferencesKey("user_calories_burn")
    }
}