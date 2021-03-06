package com.zlagi.data.di

import com.zlagi.data.repository.account.DefaultAccountRepository
import com.zlagi.data.repository.auth.DefaultAuthRepository
import com.zlagi.data.repository.feed.DefaultFeedRepository
import com.zlagi.data.repository.search.DefaultSearchBlogRepository
import com.zlagi.data.repository.search.history.DefaultHistoryRepository
import com.zlagi.domain.repository.account.AccountRepository
import com.zlagi.domain.repository.auth.AuthRepository
import com.zlagi.domain.repository.feed.FeedRepository
import com.zlagi.domain.repository.search.SearchBlogRepository
import com.zlagi.domain.repository.search.history.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(
        repository: DefaultAuthRepository
    ): AuthRepository

    @Binds
    abstract fun provideFeedRepository(
        repository: DefaultFeedRepository
    ): FeedRepository

    @Binds
    abstract fun provideSearchBlogRepository(
        repository: DefaultSearchBlogRepository
    ): SearchBlogRepository

    @Binds
    abstract fun provideHistoryRepository(
        repository: DefaultHistoryRepository
    ): HistoryRepository

    @Binds
    abstract fun provideAccountRepository(
        repository: DefaultAccountRepository
    ): AccountRepository
}
