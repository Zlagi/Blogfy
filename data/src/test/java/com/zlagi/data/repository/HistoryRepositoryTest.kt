package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.cache.FakeHistoryCacheDataSource
import com.zlagi.data.mapper.HistoryDataDomainMapper
import com.zlagi.data.repository.search.history.DefaultHistoryRepository
import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.domain.repository.search.history.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@MediumTest
class HistoryRepositoryTest {

    private lateinit var sut: HistoryRepository
    private lateinit var historyDataDomainMapper: HistoryDataDomainMapper

    @Before
    fun setup() {
        historyDataDomainMapper = HistoryDataDomainMapper()

        // systemUnderTest
        sut = DefaultHistoryRepository(
            historyCacheDataSource = FakeHistoryCacheDataSource(),
            historyDataDomainMapper = historyDataDomainMapper
        )
    }

    @Test
    fun `test getHistory() returns all saved items`() = runBlockingTest {
        // Given
        val expected =
            historyDataDomainMapper.fromList(FakeDataGenerator.historyList)

        // When
        sut.saveQuery(item = expected[0])
        sut.saveQuery(item = expected[1])
        sut.saveQuery(item = expected[2])

        val result = sut.getHistory()

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test deleteQuery() deletes the item`() = runBlockingTest {
        // Given
        val history = historyDataDomainMapper.fromList(FakeDataGenerator.historyList)

        // When
        sut.saveQuery(item = history[0])
        sut.saveQuery(item = history[1])
        sut.deleteQuery(query = history[0].query)

        val result = sut.getHistory()

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).doesNotContain(history[0])
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test clearHistory() clears the cache`() = runBlockingTest {
        // Given
        val expected = HistoryDomainModel("first")

        // When
        sut.saveQuery(item = expected)
        sut.clearHistory()

        val result = sut.getHistory()

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }
}
