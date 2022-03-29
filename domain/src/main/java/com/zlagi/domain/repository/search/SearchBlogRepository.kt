package com.zlagi.domain.repository.search

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import kotlinx.coroutines.flow.Flow

interface SearchBlogRepository {
    suspend fun requestMoreBlogs(searchQuery: String, page: Int, pageSize: Int): DataResult<PaginatedBlogsDomainModel>
    fun getBlogs(searchQuery: String): Flow<List<BlogDomainModel>>
    suspend fun storeBlogs(blogList: List<BlogDomainModel>)
    suspend fun deleteAllBlogs()
}