package com.zlagi.domain.usecase.blog.checkauthor

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckBlogAuthorUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        blogPk: Int
    ): DataResult<Boolean> {
        return when (
            val result = withContext(dispatcher) {
                feedRepository.checkBlogAuthor(blogPk)
            }) {
            is DataResult.Success -> {
                if (result.data == Constants.HAVE_PERMISSION) DataResult.Success(true)
                else DataResult.Success(false)
            }
            is DataResult.Error -> DataResult.Error(result.exception)
        }
    }
}
