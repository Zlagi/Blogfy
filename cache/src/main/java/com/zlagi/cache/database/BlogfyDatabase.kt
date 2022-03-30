package com.zlagi.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zlagi.cache.database.account.AccountDao
import com.zlagi.cache.database.feed.BlogDao
import com.zlagi.cache.database.search.SearchBlogDao
import com.zlagi.cache.database.search.suggestions.SearchSuggestionsDao
import com.zlagi.cache.model.AccountCacheModel
import com.zlagi.cache.model.BlogCacheModel
import com.zlagi.cache.model.SearchBlogCacheModel
import com.zlagi.cache.model.SearchSuggestionCacheModel

@Database(
    entities = [
        BlogCacheModel::class,
        SearchBlogCacheModel::class,
        AccountCacheModel::class,
        SearchSuggestionCacheModel::class
    ],
    version = 1
)
abstract class BlogfyDatabase : RoomDatabase() {
    abstract fun blogDao(): BlogDao
    abstract fun searchDao(): SearchBlogDao
    abstract fun accountDao(): AccountDao
    abstract fun searchSuggestionsDao(): SearchSuggestionsDao
}
