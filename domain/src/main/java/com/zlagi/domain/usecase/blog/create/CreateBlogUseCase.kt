package com.zlagi.domain.usecase.blog.create

import android.net.Uri
import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.common.utils.BlogError
import com.zlagi.common.utils.validator.BlogValidator
import com.zlagi.common.utils.validator.result.UpdateBlogResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateBlogUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        blogTitle: String,
        blogDescription: String,
        blogImage: Uri?,
        creationTime: String
    ): UpdateBlogResult {

        val titleError = if (!BlogValidator.isValidTitle(blogTitle)) BlogError.InputTooShort else null

        val descriptionError =
            if (!BlogValidator.isValidDescription(blogDescription)) BlogError.InputTooShort else null

        val imageError = if (!BlogValidator.isValidImage(blogImage)) BlogError.EmptyField else null

        if (titleError != null || descriptionError != null || imageError != null) {
            return UpdateBlogResult(titleError, descriptionError, imageError)
        }

        return when (val result = withContext(dispatcher) {
            feedRepository.createBlog(
                blogTitle,
                blogDescription,
                creationTime
            )
        }
        ) {
            is DataResult.Success -> {
                UpdateBlogResult(result = DataResult.Success(Unit))
            }
            is DataResult.Error -> {
                UpdateBlogResult(result = DataResult.Error(result.exception))
            }
        }
    }
}