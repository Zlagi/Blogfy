package com.zlagi.network.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlogNetworkModel(
    @field:Json(name ="pk") val pk: Int?,
    @field:Json(name ="username") val username: String?,
    @field:Json(name ="title") val title: String?,
    @field:Json(name ="description") val description: String?,
    @field:Json(name ="created") val created: String?,
    @field:Json(name ="updated") val updated: String?
)
