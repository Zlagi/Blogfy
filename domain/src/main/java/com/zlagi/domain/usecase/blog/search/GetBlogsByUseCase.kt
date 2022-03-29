package com.zlagi.domain.usecase.blog.search

import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBlogsByUseCase @Inject constructor(private val feedRepository: FeedRepository) {
    suspend operator fun invoke(
        query: String
    ) = feedRepository.getBlogs(query).first()
}