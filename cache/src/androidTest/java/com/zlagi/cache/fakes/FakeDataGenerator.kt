package com.zlagi.cache.fakes

import com.zlagi.data.model.AccountDataModel
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.model.SearchSuggestionDataModel

object FakeDataGenerator {
    const val emptySearchQuery = ""

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

    val account = AccountDataModel(1, "test@gmail.com", "testtest")

    val searchSuggestions = listOf(
        SearchSuggestionDataModel(1, "first"),
        SearchSuggestionDataModel(2, "second"),
        SearchSuggestionDataModel(3, "third")
    )
}
