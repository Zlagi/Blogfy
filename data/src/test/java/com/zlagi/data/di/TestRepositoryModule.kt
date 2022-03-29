package com.zlagi.data.di

import com.zlagi.data.fakes.repository.FakeAccountRepository
import com.zlagi.data.fakes.repository.FakeFeedRepositoryReturnsSuccess
import com.zlagi.data.fakes.repository.FakeSearchSuggestionsRepository
import com.zlagi.data.repository.auth.DefaultAuthRepository
import com.zlagi.domain.repository.account.AccountRepository
import com.zlagi.domain.repository.auth.AuthRepository
import com.zlagi.domain.repository.feed.FeedRepository
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
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
    abstract fun provideAuthRepository(authRepository: DefaultAuthRepository): AuthRepository

    @Binds
    abstract fun provideBlogRepository(blogRepository: FakeFeedRepositoryReturnsSuccess): FeedRepository

    @Binds
    abstract fun provideSearchSuggestionsRepository(searchSuggestionsRepository: FakeSearchSuggestionsRepository): SearchSuggestionsRepository

    @Binds
    abstract fun provideAccountRepository(accountRepository: FakeAccountRepository): AccountRepository
}
