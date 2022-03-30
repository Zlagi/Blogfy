package com.zlagi.domain.usecase.blog.search

import com.zlagi.common.exception.NetworkException
import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.PaginatedBlogsDomainModel
import com.zlagi.domain.repository.search.SearchBlogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RequestMoreBlogsUseCase @Inject constructor(
    private val searchBlogRepository: SearchBlogRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        refreshLoad: Boolean,
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel> {
        val result =
            withContext(dispatcher) { searchBlogRepository.requestMoreBlogs(searchQuery, page, pageSize) }
        return when (result) {
            is DataResult.Success -> {
                if (refreshLoad) searchBlogRepository.deleteAllBlogs()
                val results = result.data.results
                if (results.isEmpty()) return DataResult.Error(NetworkException.NoResults)
                searchBlogRepository.storeBlogs(results)
                DataResult.Success(result.data)
            }
            is DataResult.Error -> {
                DataResult.Error(result.exception)
            }
        }
    }
}