package com.zlagi.preferences.di

import com.zlagi.data.source.preferences.PreferencesDataSource
import com.zlagi.preferences.source.DefaultPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun providePreferences(preferencesDataSource: DefaultPreferencesDataSource): PreferencesDataSource
}