package com.zlagi.preferences.di

import com.zlagi.data.source.preferences.PreferencesDataSource
import com.zlagi.preferences.fakes.FakePreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PreferencesModule::class]
)
abstract class TestPreferencesModule {

    @Binds
    @Singleton
    abstract fun providePreferences(preferencesDataSource: FakePreferences): PreferencesDataSource
}
