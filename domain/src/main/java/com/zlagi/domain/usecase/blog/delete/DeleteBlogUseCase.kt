package com.zlagi.domain.usecase.blog.delete

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteBlogUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        blogPk: Int
    ) = withContext(dispatcher) {
        feedRepository.deleteBlog(blogPk)
    }
}