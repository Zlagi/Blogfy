package com.zlagi.data.di

import com.zlagi.data.fakes.repository.FakeAccountRepository
import com.zlagi.data.fakes.repository.FakeBlogRepository
import com.zlagi.data.fakes.repository.FakeHistoryRepository
import com.zlagi.data.repository.auth.DefaultAuthRepository
import com.zlagi.domain.repository.account.AccountRepository
import com.zlagi.domain.repository.auth.AuthRepository
import com.zlagi.domain.repository.feed.FeedRepository
import com.zlagi.domain.repository.search.history.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {

    @Binds
    abstract fun provideAuthRepository(
        repository: DefaultAuthRepository
    ): AuthRepository

    @Binds
    abstract fun provideBlogRepository(
        repository: FakeBlogRepository
    ): FeedRepository

    @Binds
    abstract fun provideHistoryRepository(
        repository: FakeHistoryRepository
    ): HistoryRepository

    @Binds
    abstract fun provideAccountRepository(
        repository: FakeAccountRepository
    ): AccountRepository
}
