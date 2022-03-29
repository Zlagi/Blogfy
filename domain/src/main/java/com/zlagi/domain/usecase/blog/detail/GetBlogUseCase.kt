package com.zlagi.domain.usecase.blog.detail

import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBlogUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(
        blogPk: Int
    ) = feedRepository.getBlog(blogPk).first()
}