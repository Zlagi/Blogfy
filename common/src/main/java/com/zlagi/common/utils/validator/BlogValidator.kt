package com.zlagi.common.utils.validator

import android.net.Uri

object BlogValidator {

    fun isValidTitle(input: String): Boolean =
        input.count() > 2

    fun isValidDescription(input: String): Boolean =
        input.count() > 8

    fun isValidImage(input: Uri?): Boolean =
        input != null
}
