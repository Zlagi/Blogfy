package com.zlagi.cache.source.feed

import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.mapper.FeedBlogCacheDataMapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFeedCacheDataSource @Inject constructor(
    private val feedDao: FeedDao,
    private val feedBlogCacheDataMapper: FeedBlogCacheDataMapper,
) : FeedCacheDataSource {

    override fun fetchBlog(blogPk: Int): Flow<BlogDataModel> = feedDao.fetchBlog(blogPk).map {
        feedBlogCacheDataMapper.from(it)
    }

    override fun fetchBlogs(searchQuery: String): Flow<List<BlogDataModel>> =
        feedDao.fetchBlogsOrderByTitleDESC(searchQuery).map {
            feedBlogCacheDataMapper.fromList(it)
        }

    override suspend fun storeBlog(blog: BlogDataModel) {
        feedBlogCacheDataMapper.to(blog).let {
            feedDao.storeBlog(it)
        }
    }

    override suspend fun storeBlogs(blogList: List<BlogDataModel>) {
        feedBlogCacheDataMapper.toList(blogList).let {
            feedDao.storeBlogs(it)
        }
    }

    override suspend fun updateBlog(blog: BlogDataModel) {
        feedBlogCacheDataMapper.to(blog).let {
            feedDao.updateBlog(it.pk, it.title, it.description, it.updated)
        }
    }

    override suspend fun deleteBlog(blogPk: Int) {
        feedDao.deleteBlog(blogPk)
    }

    override suspend fun deleteAllBlogs() {
        feedDao.deleteAllBlogs()
    }
}
