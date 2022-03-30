package com.zlagi.domain.usecase.blog.search

import com.zlagi.domain.repository.search.SearchBlogRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBlogsByUseCase @Inject constructor(private val searchBlogRepository: SearchBlogRepository) {
    suspend operator fun invoke(
        query: String
    ) = searchBlogRepository.getBlogs(query).first()
}