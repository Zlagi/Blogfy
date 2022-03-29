package com.zlagi.common.utils

sealed class BlogError : Error() {
    object InputTooShort : BlogError()
    object EmptyField : BlogError()
}
