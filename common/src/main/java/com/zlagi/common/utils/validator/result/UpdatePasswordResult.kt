package com.zlagi.common.utils.validator.result

import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.SimpleResource

data class UpdatePasswordResult(
    val currentPasswordError: AuthError? = null,
    val newPasswordError: AuthError? = null,
    val confirmPasswordError: AuthError? = null,
    val result: SimpleResource? = null
)
