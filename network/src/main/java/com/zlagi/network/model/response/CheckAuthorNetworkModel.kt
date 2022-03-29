package com.zlagi.network.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GenericResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "message") val message: String
)