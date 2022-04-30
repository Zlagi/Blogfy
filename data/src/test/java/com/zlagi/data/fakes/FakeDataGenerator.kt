package com.zlagi.data.fakes

import com.zlagi.data.model.*
import com.zlagi.domain.model.*

object FakeDataGenerator {
    const val email = "test@gmail.com"
    const val username = "testtest"
    const val password = "testtest"
    const val newPassword = "testtestupdated"

    val tokens = TokensDataModel(
        accessToken = "a4U8_gH4HhFghqhuq7'4HgjhHhqFfhgjqhg",
        refreshToken = "faaf33HHf3-6jJJfhFiK4j__FfHFHhf'4HgjhHhqFfhgjqhg"
    )

    const val emptySearchQuery = ""
    const val page = 1
    const val pageSize = 10
    const val blogPk = 12
    const val deleted = "Deleted"
    const val created = "Created"
    const val updated = "Updated"

    val blogCreated = BlogDataModel(
        1,
        "test1 created",
        "some random text for testing",
        "Date: 2022-01-25 Time: 18:40:14",
        "",
        "Alex",
    )

    val blogUpdated = BlogDataModel(
        1,
        "test1 updated",
        "some random text for testing",
        "Date: 2022-01-25 Time: 18:40:14",
        "Date: 2022-01-25 Time: 18:50:14",
        "Alex",
    )

    val blogs = listOf(
        BlogDataModel(
            5788,
            "test1",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 18:50:14",
            "Zlagii",
        ),
        BlogDataModel(
            5791,
            "test2",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 18:59:12",
            "Zlagii"
        ),
        BlogDataModel(
            5792,
            "test3",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 19:19:12",
            "Frank"
        )
    )

    val blogsDomain = listOf(
        BlogDomainModel(
            5788,
            "test1",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 18:50:14",
            "Zlagii",
        ),
        BlogDomainModel(
            5791,
            "test2",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 18:59:12",
            "Zlagii"
        ),
        BlogDomainModel(
            5792,
            "test3",
            "some random text for testing",
            "Date: 2022-01-25 Time: 18:40:14",
            "Date: 2022-01-25 Time: 19:19:12",
            "Frank"
        )
    )

    private val pagination = PaginationDataModel(
        currentPage = 1,
        totalPages = 1
    )

    private val paginationDomain = PaginationDomainModel(
        currentPage = 1,
        totalPages = 1
    )

    val paginatedBlogsDataModel = PaginatedBlogsDataModel(
        results = blogs,
        pagination = pagination
    )

    val paginatedBlogsDomainModel = PaginatedBlogsDomainModel(
        results = blogsDomain,
        pagination = paginationDomain
    )

    val account = AccountDataModel(1, "test@gmail.com", "testtest")
    val accountDomain = AccountDomainModel(1, "test@gmail.com", "testtest")

    val historyList = listOf(
        HistoryDataModel(query = "first"),
        HistoryDataModel(query = "second"),
        HistoryDataModel(query = "third")
    )
}
