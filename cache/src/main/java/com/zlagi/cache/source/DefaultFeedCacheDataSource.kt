package com.zlagi.cache.source

import com.zlagi.cache.database.feed.BlogDao
import com.zlagi.cache.mapper.BlogCacheDataMapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFeedCacheDataSource @Inject constructor(
    private val blogDao: BlogDao,
    private val blogCacheDataMapper: BlogCacheDataMapper,
) : FeedCacheDataSource {

    override fun fetchBlog(blogPk: Int): Flow<BlogDataModel> = blogDao.fetchBlog(blogPk).map {
        blogCacheDataMapper.from(it)
    }

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> =
        blogDao.fetchBlogsOrderByTitleDESC(searchQuery).map {
            blogCacheDataMapper.fromList(it)
        }

    override suspend fun storeBlog(blog: BlogDataModel) {
        blogCacheDataMapper.to(blog).let {
            blogDao.storeBlog(it)
        }
    }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        blogCacheDataMapper.toList(blogList).let {
            blogDao.storeBlogs(it)
        }
    }

    override suspend fun updateBlog(blog: BlogDataModel) {
        blogCacheDataMapper.to(blog).let {
            blogDao.updateBlog(it.pk, it.title, it.description, it.updated)
        }
    }

    override suspend fun deleteBlog(blogPk: Int) {
        blogDao.deleteBlog(blogPk)
    }

    override suspend fun deleteAllBlogs() {
        blogDao.deleteAllBlogs()
    }
}
