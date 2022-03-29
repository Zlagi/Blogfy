package com.zlagi.data.source.cache.search

import com.zlagi.data.model.BlogDataModel
import kotlinx.coroutines.flow.Flow

interface SearchBlogCacheDataSource {
    fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>>
    suspend fun storeBlogs(blogList: List<BlogDataModel>)
    suspend fun deleteAllBlogs()
}
