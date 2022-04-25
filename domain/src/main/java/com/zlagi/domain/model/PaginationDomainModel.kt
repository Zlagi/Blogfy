package com.zlagi.domain.model

data class PaginationDomainModel(
    val currentPage: Int,
    val totalPages: Int
){

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    val canLoadMore: Boolean
        get() = currentPage < totalPages
}
