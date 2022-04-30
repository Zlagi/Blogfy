package com.zlagi.presentation.fakes

import com.zlagi.domain.model.*

object FakeDataGenerator {
    const val correctEmail = "test@gmail.com"
    const val correctUsername = "Alex"
    const val incorrectUsername = "Al&"
    const val incorrectUsername2 = "Al"
    const val incorrectEmail = "testgmail.com"
    const val correctPassword = "testtest"
    const val incorrectPassword = "testte"
    const val oldPassword = "testte"
    const val newPassword = "testterrrr"

    const val blogPk = 12
    const val blogTitle = "test1 updated"
    const val blogDescription = "some random text for testing"
    const val blogImage =
        "file:///data/user/0/com.zlagi.blogfy/cache/cropped6410783516901492573.jpg"

    val blogs = listOf(
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

    val blogMissingTitle = BlogDomainModel(
        5788,
        "",
        "some random text for testing",
        "Date: 2022-01-25 Time: 18:40:14",
        "Date: 2022-01-25 Time: 18:59:12",
        "Zlagii"
    )

    val blogMissingDescription = BlogDomainModel(
        5788,
        "test1",
        "",
        "Date: 2022-01-25 Time: 18:40:14",
        "Date: 2022-01-25 Time: 18:59:12",
        "Zlagii"
    )

    val oldBlog = BlogDomainModel(
        5788,
        "test1",
        "some random text for testing",
        "Date: 2022-01-25 Time: 18:40:14",
        "Date: 2022-01-25 Time: 18:59:12",
        "Zlagii"
    )

    val updatedBlog = BlogDomainModel(
        5788,
        "test1 updated",
        "some random text for testing updated",
        "Date: 2022-01-25 Time: 18:40:14",
        "Date: 2022-01-25 Time: 18:59:12",
        "Zlagii"
    )

    const val query = "something"

    private val pagination = PaginationDomainModel(
        currentPage = 1,
        totalPages = 2
    )

    val paginatedBlogsDomainModel = PaginatedBlogsDomainModel(
        results = blogs,
        pagination = pagination
    )

    val history = listOf(
        HistoryDomainModel(query = "first"),
        HistoryDomainModel(query = "second"),
        HistoryDomainModel(query = "third")
    )

    val account = AccountDomainModel(pk = 1, email = "test@gmail.com", username = "testtest")
}
