package com.zlagi.network.di

import com.zlagi.data.connectivity.ConnectivityChecker
import com.zlagi.network.fakes.FakeConnectivityCheckReturnSuccess
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ConnectivityModule::class]
)
abstract class TestConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityChecker(connectivityChecker: FakeConnectivityCheckReturnSuccess): ConnectivityChecker
}