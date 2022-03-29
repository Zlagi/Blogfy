package com.zlagi.data.fakes.source.cache

import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFeedCacheDataSource : FeedCacheDataSource {

    override fun fetchBlog(blogPk: Int): Flow<BlogDataModel> {
        return flow {
            emit(FakeDataGenerator.blogCreated)
        }
    }

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> {
        return flow {
            emit(FakeDataGenerator.blogs)
        }
    }

    override suspend fun storeBlog(blog: BlogDataModel) {
        //
    }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        //
    }

    override suspend fun updateBlog(blog: BlogDataModel) {
        //
    }

    override suspend fun deleteBlog(blogPk: Int) {
        //
    }

    override suspend fun deleteAllBlogs() {
        //
    }
}
