package com.zlagi.cache.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.search.suggestions.SearchSuggestionsDao
import com.zlagi.cache.fakes.FakeDataGenerator
import com.zlagi.cache.mapper.SearchSuggestionCacheDataMapper
import com.zlagi.data.model.SearchSuggestionDataModel
import com.zlagi.data.source.cache.search.suggestions.SearchSuggestionsCacheDataSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@HiltAndroidTest
@MediumTest
class DefaultSearchSuggestionsCacheDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var searchSuggestionsCacheDataSource: SearchSuggestionsCacheDataSource

    @Inject
    @Named("test_db")
    lateinit var database: BlogfyDatabase

    private lateinit var searchSuggestionsDao: SearchSuggestionsDao

    private lateinit var searchSuggestionCacheDataMapper: SearchSuggestionCacheDataMapper

    @Before
    fun setup() {
        hiltRule.inject()
        searchSuggestionsDao = database.searchSuggestionsDao()
        searchSuggestionCacheDataMapper = SearchSuggestionCacheDataMapper()
        searchSuggestionsCacheDataSource = DefaultSearchSuggestionsCacheDataSource(
            searchSuggestionsDao,
            searchSuggestionCacheDataMapper
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun storeSearchSuggestion_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.searchSuggestions[0]

        // When
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[0])

        // Then
        searchSuggestionsCacheDataSource.fetchSearchSuggestions().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteSearchSuggestion_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.searchSuggestions[2]

        // When
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[0])
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[1])
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[2])
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(FakeDataGenerator.searchSuggestions[0].pk)
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(FakeDataGenerator.searchSuggestions[1].pk)

        // Then
        searchSuggestionsCacheDataSource.fetchSearchSuggestions().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteAllSearchSuggestions_success() = runBlockingTest {
        // Given
        val expected = emptyList<SearchSuggestionDataModel>()

        // When
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[0])
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[1])
        searchSuggestionsCacheDataSource.storeSearchSuggestion(FakeDataGenerator.searchSuggestions[2])
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(FakeDataGenerator.searchSuggestions[0].pk)
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(FakeDataGenerator.searchSuggestions[1].pk)
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(FakeDataGenerator.searchSuggestions[2].pk)

        // Then
        searchSuggestionsCacheDataSource.fetchSearchSuggestions().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
