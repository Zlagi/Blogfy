package com.zlagi.common.utils.result

import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.SimpleResource

data class SignInResult(
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null
)
