package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.cache.FakeSearchSuggestionsCacheDataSource
import com.zlagi.data.mapper.SearchSuggestionDataDomainMapper
import com.zlagi.data.repository.search.suggestions.DefaultSearchSuggestionsRepository
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@MediumTest
class SearchSuggestionsRepositoryTest {

    private lateinit var sut: SearchSuggestionsRepository
    private lateinit var searchSuggestionDataDomainMapper: SearchSuggestionDataDomainMapper

    @Before
    fun setup() {
        searchSuggestionDataDomainMapper = SearchSuggestionDataDomainMapper()

        // systemUnderTest
        sut = DefaultSearchSuggestionsRepository(
            searchSuggestionsCacheDataSource = FakeSearchSuggestionsCacheDataSource(),
            searchSuggestionDataDomainMapper = searchSuggestionDataDomainMapper
        )
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @Test
    fun `getSearchSuggestions() returns searchSuggestions`() = runBlockingTest {
        // Given
        val expected =
            searchSuggestionDataDomainMapper.fromList(FakeDataGenerator.searchSuggestions)

        // When
        val result = sut.getSearchSuggestions()

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
