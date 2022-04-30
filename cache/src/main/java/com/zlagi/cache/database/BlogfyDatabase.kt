package com.zlagi.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.database.search.SearchDao
import com.zlagi.cache.database.search.history.HistoryDao
import com.zlagi.cache.model.AccountCacheModel
import com.zlagi.cache.model.FeedBlogCacheModel
import com.zlagi.cache.model.SearchBlogCacheModel
import com.zlagi.cache.model.HistoryCacheModel

@Database(
    entities = [
        FeedBlogCacheModel::class,
        SearchBlogCacheModel::class,
        AccountCacheModel::class,
        HistoryCacheModel::class
    ],
    version = 1
)
abstract class BlogfyDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun searchDao(): SearchDao
    abstract fun historyDao(): HistoryDao
    abstract fun accountDao(): AccountDao
}
