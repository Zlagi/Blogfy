package com.zlagi.domain.usecase.blog.create

import android.net.Uri
import com.zlagi.common.qualifier.IoDispatcher
import com.zlagi.domain.validator.BlogValidator
import com.zlagi.common.utils.result.UpdateBlogResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateBlogUseCase @Inject constructor(
    private val repository: FeedRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        blogTitle: String,
        blogDescription: String,
        blogImage: Uri?,
        creationTime: String
    ): UpdateBlogResult {

        val titleError = BlogValidator.blogTitleError(blogTitle)
        val descriptionError = BlogValidator.blogDescriptionError(blogDescription)
        val imageError = BlogValidator.blogImageError(blogImage)

        if (titleError != null || descriptionError != null || imageError != null) {
            return UpdateBlogResult(titleError, descriptionError, imageError)
        }

        return when (
            val result = withContext(dispatcher) {
                repository.createBlog(
                    blogTitle,
                    blogDescription,
                    creationTime
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
