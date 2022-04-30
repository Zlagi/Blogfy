package com.zlagi.cache.source.search

import com.zlagi.cache.database.search.SearchDao
import com.zlagi.cache.mapper.SearchBlogCacheDataMapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchBlogCacheDataSource @Inject constructor(
    private val searchDao: SearchDao,
    private val searchBlogCacheDataMapper: SearchBlogCacheDataMapper,
) : SearchBlogCacheDataSource {

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> =
        searchDao.fetchBlogsOrderByTitleDESC(searchQuery).map {
            searchBlogCacheDataMapper.fromList(it)
        }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        searchBlogCacheDataMapper.toList(blogList).let {
            searchDao.storeBlogs(it)
        }
    }

    override suspend fun deleteAllBlogs() {
        searchDao.deleteAllBlogs()
    }
}
