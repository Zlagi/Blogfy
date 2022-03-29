package com.zlagi.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueueResponse(response: String, code: Int) {
    enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody(response)
    )
}
