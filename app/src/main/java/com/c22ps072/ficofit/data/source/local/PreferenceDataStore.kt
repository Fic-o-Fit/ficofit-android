package com.c22ps072.ficofit.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class PreferenceDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
}