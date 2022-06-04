package com.c22ps072.ficofit.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_REFRESH_TOKEN = stringPreferencesKey("user_refresh_token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

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

    fun getUserName(): Flow<String> = dataStore.data.map { it[USER_NAME_KEY] ?: "" }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun clearCache() {
        dataStore.edit {
            it.clear()
        }
    }
}