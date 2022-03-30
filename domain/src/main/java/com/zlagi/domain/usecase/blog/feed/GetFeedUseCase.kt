package com.zlagi.domain.usecase.blog.feed

import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(private val feedRepository: FeedRepository) {
    suspend operator fun invoke(
        query: String
    ) = feedRepository.getBlogs(query).first()
}