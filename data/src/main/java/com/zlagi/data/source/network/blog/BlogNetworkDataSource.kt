package com.zlagi.data.source.network.blog

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.model.PaginatedBlogsDataModel

interface BlogNetworkDataSource {
    suspend fun getBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDataModel>

    suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<BlogDataModel>

    suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<BlogDataModel>

    suspend fun deleteBlog(blogPk: Int): DataResult<String>
    suspend fun checkBlogAuthor(blogPk: Int): DataResult<String>
    suspend fun sendNotification(title: String)
}
