package com.zlagi.common.utils.result

import com.zlagi.common.utils.BlogError
import com.zlagi.common.utils.wrapper.SimpleResource

data class UpdateBlogResult(
    val titleError: BlogError? = null,
    val descriptionError: BlogError? = null,
    val uriError: BlogError? = null,
    val result: SimpleResource? = null
)
