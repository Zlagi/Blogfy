package com.zlagi.data.model

data class PaginatedBlogsDataModel(
    val results: List<BlogDataModel>,
    val pagination: PaginationDataModel
)
