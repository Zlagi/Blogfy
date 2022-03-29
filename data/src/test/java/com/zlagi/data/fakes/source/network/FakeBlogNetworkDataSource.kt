package com.zlagi.data.fakes.source.network

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.model.PaginatedBlogsDataModel
import com.zlagi.data.source.network.blog.BlogNetworkDataSource

class FakeBlogNetworkDataSource(
    private val paginatedBlogs: DataResult<PaginatedBlogsDataModel>,
    private val createdBlog: DataResult<BlogDataModel>,
    private val updatedBlog: DataResult<BlogDataModel>,
    private val deletedBlog: DataResult<String>,
    private val checkedAuthor: DataResult<String>
) : BlogNetworkDataSource {

    override suspend fun getBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDataModel> {
        return paginatedBlogs
    }

    override suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<BlogDataModel> {
        return createdBlog
    }

    override suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<BlogDataModel> {
        return updatedBlog
    }

    override suspend fun deleteBlog(blogPk: Int): DataResult<String> {
        return deletedBlog
    }

    override suspend fun checkBlogAuthor(blogPk: Int): DataResult<String> {
        return checkedAuthor
    }

    override suspend fun sendNotification(title: String) {
        //
    }
}
