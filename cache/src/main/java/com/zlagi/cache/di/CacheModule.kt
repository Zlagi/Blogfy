package com.zlagi.cache.di

import android.content.Context
import androidx.room.Room
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.database.search.SearchBlogDao
import com.zlagi.cache.database.search.suggestions.SearchSuggestionsDao
import com.zlagi.cache.source.DefaultAccountCacheDataSource
import com.zlagi.cache.source.DefaultFeedCacheDataSource
import com.zlagi.cache.source.DefaultSearchBlogCacheDataSource
import com.zlagi.cache.source.DefaultSearchSuggestionsCacheDataSource
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import com.zlagi.data.source.cache.search.suggestions.SearchSuggestionsCacheDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {

    @Binds
    abstract fun provideFeedCacheDataSource(
        datasource: DefaultFeedCacheDataSource
    ): FeedCacheDataSource

    @Binds
    abstract fun provideSearchBlogCacheDataSource(
        datasource: DefaultSearchBlogCacheDataSource
    ): SearchBlogCacheDataSource

    @Binds
    abstract fun provideAccountCacheDataSource(
        datasource: DefaultAccountCacheDataSource
    ): AccountCacheDataSource

    @Binds
    abstract fun provideSearchSuggestionsCacheDataSource(
        datasource: DefaultSearchSuggestionsCacheDataSource
    ): SearchSuggestionsCacheDataSource

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): BlogfyDatabase {
            return Room.databaseBuilder(
                context,
                BlogfyDatabase::class.java,
                "blogfy.db"
            )
                .build()
        }

        @Provides
        fun provideBlogDao(
            blogfyDatabase: BlogfyDatabase
        ): FeedDao = blogfyDatabase.blogDao()

        @Provides
        fun provideSearchBlogDao(
            blogfyDatabase: BlogfyDatabase
        ): SearchBlogDao = blogfyDatabase.searchDao()

        @Provides
        fun provideAccountDao(
            blogfyDatabase: BlogfyDatabase
        ): AccountDao = blogfyDatabase.accountDao()

        @Provides
        fun provideSearchSuggestionsDao(
            blogfyDatabase: BlogfyDatabase
        ): SearchSuggestionsDao = blogfyDatabase.searchSuggestionsDao()
    }
}
