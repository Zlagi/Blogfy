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
    private val repository: SearchBlogRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        refreshLoad: Boolean,
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDomainModel> {
        return when (
            val result = withContext(dispatcher) {
                repository.requestMoreBlogs(searchQuery, page, pageSize)
            }) {
            is DataResult.Success -> {
                if (refreshLoad) repository.deleteAllBlogs()
                val results = result.data.results
                if (results.isEmpty()) return DataResult.Error(NetworkException.NoResults)
                repository.storeBlogs(results)
                DataResult.Success(result.data)
            }
            is DataResult.Error -> {
                DataResult.Error(result.exception)
            }
        }
    }
}
