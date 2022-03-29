package com.zlagi.domain.usecase.blog.feed

import com.zlagi.common.exception.NetworkException
import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RequestMoreBlogsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        refreshLoad: Boolean,
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel> {
        val result =
            withContext(dispatcher) { feedRepository.requestMoreBlogs(searchQuery, page, pageSize) }
        return when (result) {
            is DataResult.Success -> {
                if (refreshLoad) feedRepository.deleteAllBlogs()
                val feed = result.data.results
                if (feed.isEmpty()) return DataResult.Error(NetworkException.NoResults)
                feedRepository.storeBlogs(feed)
                DataResult.Success(result.data)
            }
            is DataResult.Error -> {
                DataResult.Error(result.exception)
            }
        }
    }
}