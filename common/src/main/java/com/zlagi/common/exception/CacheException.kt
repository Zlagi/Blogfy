package com.zlagi.common.exception

sealed class CacheException: Exception() {
    object NoResults : NetworkException()
}
