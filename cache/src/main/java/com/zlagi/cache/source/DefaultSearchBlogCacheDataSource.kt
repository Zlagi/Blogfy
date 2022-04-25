package com.zlagi.cache.source

import com.zlagi.cache.database.search.SearchBlogDao
import com.zlagi.cache.mapper.SearchBlogCacheDataMapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchBlogCacheDataSource @Inject constructor(
    private val searchBlogDao: SearchBlogDao,
    private val searchBlogCacheDataMapper: SearchBlogCacheDataMapper,
) : SearchBlogCacheDataSource {

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> =
        searchBlogDao.fetchBlogsOrderByTitleDESC(searchQuery).map {
            searchBlogCacheDataMapper.fromList(it)
        }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        searchBlogCacheDataMapper.toList(blogList).let {
            searchBlogDao.storeBlogs(it)
        }
    }

    override suspend fun deleteAllBlogs() {
        searchBlogDao.deleteAllBlogs()
    }
}
