package com.zlagi.domain.validator

import android.net.Uri
import com.zlagi.common.utils.BlogError

object BlogValidator {

    private const val titleLength = 2
    private const val descriptionLength = 8

    fun blogTitleError(title: String): BlogError? {
        return if (!isValidTitle(title)) BlogError.InputTooShort else null
    }

    fun blogDescriptionError(description: String): BlogError? {
        return if (!isValidDescription(description)) BlogError.InputTooShort else null
    }

    fun blogImageError(image: Uri?): BlogError? {
        return if (!isValidImage(image)) BlogError.EmptyField else null
    }

    private fun isValidTitle(input: String): Boolean =
        input.count() > titleLength

    private fun isValidDescription(input: String): Boolean =
        input.count() > descriptionLength

    private fun isValidImage(input: Uri?): Boolean =
        input != null
}
