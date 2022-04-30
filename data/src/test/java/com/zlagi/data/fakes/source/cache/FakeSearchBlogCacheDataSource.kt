package com.zlagi.data.fakes.source.cache

import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSearchBlogCacheDataSource: SearchBlogCacheDataSource {

    private val cache = LinkedHashMap<String, BlogDataModel>()

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> {
        return flow {
            emit(value = cache.values.toList())
        }
    }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        blogList.map {
            cache[it.pk.toString()] = it
        }
    }

    override suspend fun deleteAllBlogs() {
        cache.clear()
    }
}