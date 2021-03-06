package com.zlagi.cache.di

import android.content.Context
import androidx.room.Room
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.search.SearchDao
import com.zlagi.cache.database.search.history.HistoryDao
import com.zlagi.cache.source.account.DefaultAccountCacheDataSource
import com.zlagi.cache.source.feed.DefaultFeedCacheDataSource
import com.zlagi.cache.source.search.history.DefaultHistoryCacheDataSource
import com.zlagi.data.source.cache.account.AccountCacheDataSource
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CacheModule::class]
)
abstract class TestCacheModule {

    @Binds
    abstract fun provideBlogCacheDataSource(
        datasource: DefaultFeedCacheDataSource
    ): FeedCacheDataSource

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
        @Named("test_db")
        fun provideInMemoryDatabase(@ApplicationContext context: Context): BlogfyDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                BlogfyDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }

        @Provides
        fun provideBlogDao(
            blogfyDatabase: BlogfyDatabase
        ): FeedDao = blogfyDatabase.feedDao()

        @Provides
        fun provideAccountDao(
            blogfyDatabase: BlogfyDatabase
        ): AccountDao = blogfyDatabase.accountDao()


        @Provides
        fun provideSearchDao(
            blogfyDatabase: BlogfyDatabase
        ): SearchDao = blogfyDatabase.searchDao()

        @Provides
        fun provideHistoryDao(
            blogfyDatabase: BlogfyDatabase
        ): HistoryDao = blogfyDatabase.historyDao()
    }
}
