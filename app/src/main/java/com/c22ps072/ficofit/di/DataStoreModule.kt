package com.c22ps072.ficofit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c22ps072.ficofit.data.source.local.PreferenceDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")
@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    fun provideDataStore(@ApplicationContext context: Context) : DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideAuthPreference(dataStore: DataStore<Preferences>) :PreferenceDataStore =
        PreferenceDataStore(dataStore)

}