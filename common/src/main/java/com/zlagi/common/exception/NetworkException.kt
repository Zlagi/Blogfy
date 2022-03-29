package com.zlagi.common.exception

sealed class NetworkException : Exception() {
    object NetworkUnavailable : NetworkException()
    object Network : NetworkException()
    object NotFound : NetworkException()
    object BadRequest : NetworkException()
    object NotAuthorized : NetworkException()
    object ServiceNotWorking : NetworkException()
    object ServiceUnavailable : NetworkException()
    object NoResults : NetworkException()
    object Unknown : NetworkException()
    object UnknownError : NetworkException()
}
