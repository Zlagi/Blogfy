package com.zlagi.cache.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.search.history.HistoryDao
import com.zlagi.cache.fakes.FakeDataGenerator
import com.zlagi.cache.mapper.HistoryCacheDataMapper
import com.zlagi.cache.source.search.history.DefaultHistoryCacheDataSource
import com.zlagi.data.model.HistoryDataModel
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
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
class DefaultHistoryCacheDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var historyCacheDataSource: HistoryCacheDataSource

    @Inject
    @Named("test_db")
    lateinit var database: BlogfyDatabase

    private lateinit var historyDao: HistoryDao

    private lateinit var historyCacheDataMapper: HistoryCacheDataMapper

    @Before
    fun setup() {
        hiltRule.inject()
        historyDao = database.historyDao()
        historyCacheDataMapper = HistoryCacheDataMapper()
        historyCacheDataSource = DefaultHistoryCacheDataSource(
            historyDao,
            historyCacheDataMapper
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun test_getHistory_returns_all_saved_items() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.historyList

        // When
        historyCacheDataSource.saveQuery(FakeDataGenerator.historyList[0])
        historyCacheDataSource.saveQuery(FakeDataGenerator.historyList[1])
        historyCacheDataSource.saveQuery(FakeDataGenerator.historyList[2])

        // Then
        historyCacheDataSource.getHistory().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            expectNoEvents()
        }
    }

    @Test
    fun test_deleteQuery_deletes_the_item() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.historyList[2]

        // When
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[0])
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[1])
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[2])
        historyCacheDataSource.deleteQuery(query = FakeDataGenerator.historyList[0].query)
        historyCacheDataSource.deleteQuery(query = FakeDataGenerator.historyList[1].query)

        // Then
        historyCacheDataSource.getHistory().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            expectNoEvents()
        }
    }

    @Test
    fun test_clearHistory_clears_the_cache() = runBlockingTest {
        // Given
        val expected = emptyList<HistoryDataModel>()

        // When
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[0])
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[1])
        historyCacheDataSource.saveQuery(item = FakeDataGenerator.historyList[2])
        historyCacheDataSource.deleteQuery(query = FakeDataGenerator.historyList[0].query)
        historyCacheDataSource.deleteQuery(query = FakeDataGenerator.historyList[1].query)
        historyCacheDataSource.deleteQuery(query = FakeDataGenerator.historyList[2].query)

        // Then
        historyCacheDataSource.getHistory().test {
            val actual = awaitItem()

            // Assertion
            actual.map {
                Truth.assertThat(it).isEqualTo(expected)
            }
            expectNoEvents()
        }
    }
}
