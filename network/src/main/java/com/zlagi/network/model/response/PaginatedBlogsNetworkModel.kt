package com.zlagi.network.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginatedBlogsNetworkModel(
    @field:Json(name = "results") val results: List<BlogNetworkModel>?,
    @field:Json(name = "pagination") val pagination: PaginationNetworkModel?
)

@JsonClass(generateAdapter = true)
data class PaginationNetworkModel(
    @field:Json(name = "current_page") val current_page: Int?,
    @field:Json(name = "total_pages") val total_pages: Int?
)