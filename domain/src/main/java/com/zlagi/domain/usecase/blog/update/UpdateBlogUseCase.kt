package com.zlagi.domain.usecase.blog.update

import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.domain.validator.BlogValidator
import com.zlagi.common.utils.result.UpdateBlogResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateBlogUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): UpdateBlogResult {

        val titleError = BlogValidator.blogTitleError(blogTitle)
        val descriptionError = BlogValidator.blogDescriptionError(blogDescription)

        if (titleError != null || descriptionError != null) {
            return UpdateBlogResult(titleError, descriptionError)
        }

        return when (
            val result = withContext(dispatcher) {
                feedRepository.updateBlog(
                    blogPk,
                    blogTitle,
                    blogDescription,
                    updateTime
                )
            }) {
            is DataResult.Success -> {
                UpdateBlogResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                UpdateBlogResult(result = DataResult.Error(result.exception))
            }
        }

    }
}
