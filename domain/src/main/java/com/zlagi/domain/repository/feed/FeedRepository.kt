package com.zlagi.domain.repository.feed

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun requestMoreBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel>

    fun getBlog(blogPk: Int): Flow<BlogDomainModel>
    fun getBlogs(searchQuery: String): Flow<List<BlogDomainModel>>
    suspend fun storeBlogs(blogList: List<BlogDomainModel>)
    suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<String>

    suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<String>

    suspend fun deleteBlog(blogPk: Int): DataResult<String>
    suspend fun deleteAllBlogs()
    suspend fun checkBlogAuthor(blogPk: Int): DataResult<String>
}
