package com.zlagi.cache.di

import android.content.Context
import androidx.room.Room
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.database.search.SearchDao
import com.zlagi.cache.database.search.history.HistoryDao
import com.zlagi.cache.source.account.DefaultAccountCacheDataSource
import com.zlagi.cache.source.feed.DefaultFeedCacheDataSource
import com.zlagi.cache.source.search.DefaultSearchBlogCacheDataSource
import com.zlagi.cache.source.search.history.DefaultHistoryCacheDataSource
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
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
    abstract fun provideHistoryCacheDataSource(
        datasource: DefaultHistoryCacheDataSource
    ): HistoryCacheDataSource

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
        ): FeedDao = blogfyDatabase.feedDao()

        @Provides
        fun provideSearchBlogDao(
            blogfyDatabase: BlogfyDatabase
        ): SearchDao = blogfyDatabase.searchDao()

        @Provides
        fun provideAccountDao(
            blogfyDatabase: BlogfyDatabase
        ): AccountDao = blogfyDatabase.accountDao()

        @Provides
        fun provideHistoryDao(
            blogfyDatabase: BlogfyDatabase
        ): HistoryDao = blogfyDatabase.historyDao()
    }
}
