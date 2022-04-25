package com.zlagi.data.fakes.repository

import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeBlogRepository @Inject constructor() : FeedRepository {

    override suspend fun requestMoreBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel> {
        return DataResult.Success(FakeDataGenerator.paginatedBlogsDomainModel)
    }

    override fun getBlog(blogPk: Int): Flow<BlogDomainModel> {
        return flow {
            emit(FakeDataGenerator.blogsDomain[0])
        }
    }

    override fun getBlogs(searchQuery: String): Flow<List<BlogDomainModel>> {
        return flow {
            emit(FakeDataGenerator.blogsDomain)
        }
    }

    override suspend fun storeBlogs(blogList: List<BlogDomainModel>) {
        // Not yet implemented
    }

    override suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<String> {
        return DataResult.Success("Created")
    }

    override suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<String> {
        return DataResult.Success("Updated")
    }

    override suspend fun deleteBlog(blogPk: Int): DataResult<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllBlogs() {
        // Not yet implemented
    }

    override suspend fun checkBlogAuthor(blogPk: Int): DataResult<String> {
        return DataResult.Success(Constants.HAVE_PERMISSION)
    }
}
