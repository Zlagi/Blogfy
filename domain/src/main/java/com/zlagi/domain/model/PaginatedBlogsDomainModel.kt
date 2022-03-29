package com.zlagi.domain.model

data class PaginatedBlogsDomainModel(
    val results: List<BlogDomainModel>,
    val pagination: PaginationDomainModel
)