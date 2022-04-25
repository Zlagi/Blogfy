package com.zlagi.data.repository.search

import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.mapper.BlogDataDomainMapper
import com.zlagi.data.mapper.PaginationDataDomainMapper
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import com.zlagi.domain.repository.search.SearchBlogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchBlogRepository @Inject constructor(
    private val blogNetworkDataSource: BlogNetworkDataSource,
    private val searchBlogCacheDataSource: SearchBlogCacheDataSource,
    private val blogDataDomainMapper: BlogDataDomainMapper,
    private val paginationDataDomainMapper: PaginationDataDomainMapper
) : SearchBlogRepository {

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

    override fun getBlogs(searchQuery: String): Flow<List<BlogDomainModel>> =
        searchBlogCacheDataSource.fetchBlogs(searchQuery).map {
            blogDataDomainMapper.fromList(it)
        }


    override suspend fun storeBlogs(blogList: List<BlogDomainModel>) {
        blogDataDomainMapper.toList(blogList).let {
            searchBlogCacheDataSource.storeBlogs(it)
        }
    }

    override suspend fun deleteAllBlogs() {
        searchBlogCacheDataSource.deleteAllBlogs()
    }
}
