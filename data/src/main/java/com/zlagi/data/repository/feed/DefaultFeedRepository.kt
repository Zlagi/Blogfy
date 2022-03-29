package com.zlagi.data.repository.feed

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.mapper.BlogDataDomainMapper
import com.zlagi.data.mapper.PaginationDataDomainMapper
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFeedRepository @Inject constructor(
    private val blogNetworkDataSource: BlogNetworkDataSource,
    private val feedCacheDataSource: FeedCacheDataSource,
    private val blogDataDomainMapper: BlogDataDomainMapper,
    private val paginationDataDomainMapper: PaginationDataDomainMapper
) : FeedRepository {

    override suspend fun requestMoreBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel> {
        return when (val result = blogNetworkDataSource.getBlogs(searchQuery, page, pageSize)) {
            is DataResult.Success -> DataResult.Success(
                PaginatedBlogsDomainModel(
                    blogDataDomainMapper.fromList(result.data.results),
                    paginationDataDomainMapper.from(result.data.pagination)
                )
            )
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override fun getBlog(blogPk: Int): Flow<BlogDomainModel> =
        feedCacheDataSource.fetchBlog(blogPk).map {
            blogDataDomainMapper.from(it)
        }

    override fun getBlogs(searchQuery: String): Flow<List<BlogDomainModel>> =
        feedCacheDataSource.fetchBlogs(searchQuery).map {
            blogDataDomainMapper.fromList(it)
        }

    override suspend fun storeBlogs(blogList: List<BlogDomainModel>) {
        blogDataDomainMapper.toList(blogList).let {
            feedCacheDataSource.storeBlogs(it)
        }
    }

    override suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<String> {
        return when (
            val result =
                blogNetworkDataSource.createBlog(blogTitle, blogDescription, creationTime)
        ) {
            is DataResult.Success -> {
                blogNetworkDataSource.sendNotification(blogTitle)
                feedCacheDataSource.storeBlog(result.data)
                DataResult.Success("Created")
            }
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<String> {
        return when (val result = blogNetworkDataSource.updateBlog(blogPk, blogTitle, blogDescription, updateTime)) {
            is DataResult.Success -> {
                feedCacheDataSource.updateBlog(result.data)
                DataResult.Success("Updated")
            }
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override suspend fun deleteBlog(blogPk: Int): DataResult<String> {
        return when (val result = blogNetworkDataSource.deleteBlog(blogPk)) {
            is DataResult.Success -> {
                feedCacheDataSource.deleteBlog(blogPk)
                DataResult.Success("Deleted")
            }
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }

    override suspend fun deleteAllBlogs() {
        feedCacheDataSource.deleteAllBlogs()
    }

    override suspend fun checkBlogAuthor(blogPk: Int): DataResult<String> {
        return when (val result = blogNetworkDataSource.checkBlogAuthor(blogPk)) {
            is DataResult.Success -> DataResult.Success(result.data)
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }
}
