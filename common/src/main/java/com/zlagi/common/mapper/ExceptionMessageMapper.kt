package com.zlagi.common.mapper

import com.zlagi.common.R
import com.zlagi.common.exception.NetworkException

fun Throwable.getStringResId(): Int {
    return when (this) {
        is NetworkException.NetworkUnavailable -> R.string.network_unavailable_message
        is NetworkException.NotFound -> R.string.unknown_network_error_message
        is NetworkException.BadRequest -> R.string.email_unavailable_message
        is NetworkException.Network -> R.string.server_unreachable_message
        is NetworkException.NotAuthorized -> R.string.invalid_credentials_message
        is NetworkException.NoResults -> R.string.no_more_results
        is NetworkException.ServiceNotWorking, is NetworkException.ServiceUnavailable -> R.string.service_unavailable_message
        is NetworkException.Unknown -> R.string.unknown_network_error_message
        is UnknownError -> R.string.unknown_error_message
        else -> R.string.unknown_error_message
    }
}
