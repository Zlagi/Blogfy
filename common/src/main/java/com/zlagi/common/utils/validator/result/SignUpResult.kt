package com.zlagi.common.utils.validator.result

import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.SimpleResource

data class SignUpResult(
    val emailError: AuthError? = null,
    val usernameError: AuthError? = null,
    val passwordError: AuthError? = null,
    val confirmPasswordError: AuthError? = null,
    val result: SimpleResource? = null
)
