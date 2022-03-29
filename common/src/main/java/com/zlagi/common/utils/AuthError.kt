package com.zlagi.common.utils

sealed class AuthError : Error() {
    object EmptyField : AuthError()
    object InputTooShort : AuthError()
    object InvalidEmail : AuthError()
    object InvalidUsername : AuthError()
    object InvalidPassword : AuthError()
    object UnmatchedPassword : AuthError()
}
