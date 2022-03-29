package com.zlagi.data.di

import com.zlagi.data.repository.account.DefaultAccountRepository
import com.zlagi.data.repository.auth.DefaultAuthRepository
import com.zlagi.data.repository.feed.DefaultFeedRepository
import com.zlagi.data.repository.search.DefaultSearchBlogRepository
import com.zlagi.data.repository.search.suggestions.DefaultSearchSuggestionsRepository
import com.zlagi.domain.repository.account.AccountRepository
import com.zlagi.domain.repository.auth.AuthRepository
import com.zlagi.domain.repository.feed.FeedRepository
import com.zlagi.domain.repository.search.SearchBlogRepository
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(authRepository: DefaultAuthRepository): AuthRepository

    @Binds
    abstract fun provideFeedRepository(feedRepository: DefaultFeedRepository): FeedRepository

    @Binds
    abstract fun provideSearchBlogRepository(searchBLogRepository: DefaultSearchBlogRepository): SearchBlogRepository

    @Binds
    abstract fun provideSearchSuggestionsRepository(searchSuggestionsRepository: DefaultSearchSuggestionsRepository): SearchSuggestionsRepository

    @Binds
    abstract fun provideAccountRepository(accountRepository: DefaultAccountRepository): AccountRepository
}
