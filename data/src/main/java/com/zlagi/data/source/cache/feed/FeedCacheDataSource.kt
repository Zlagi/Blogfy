package com.zlagi.data.source.cache.feed

import com.zlagi.data.model.BlogDataModel
import kotlinx.coroutines.flow.Flow

interface FeedCacheDataSource {
    fun fetchBlog(blogPk: Int): Flow<BlogDataModel>
    fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>>
    suspend fun storeBlog(blog: BlogDataModel)
    suspend fun storeBlogs(blogList: List<BlogDataModel>)
    suspend fun updateBlog(blog: BlogDataModel)
    suspend fun deleteBlog(blogPk: Int)
    suspend fun deleteAllBlogs()
}
