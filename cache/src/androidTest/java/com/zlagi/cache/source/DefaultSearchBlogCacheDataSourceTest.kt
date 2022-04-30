package com.zlagi.cache.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.database.search.SearchDao
import com.zlagi.cache.fakes.FakeDataGenerator
import com.zlagi.cache.mapper.SearchBlogCacheDataMapper
import com.zlagi.cache.source.search.DefaultSearchBlogCacheDataSource
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.search.SearchBlogCacheDataSource
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
class DefaultSearchBlogCacheDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var searchBlogCacheSource: SearchBlogCacheDataSource

    @Inject
    @Named("test_db")
    lateinit var database: BlogfyDatabase

    private lateinit var searchDao: SearchDao

    private lateinit var searchBlogCacheDataMapper: SearchBlogCacheDataMapper

    @Before
    fun setup() {
        hiltRule.inject()
        searchDao = database.searchDao()
        searchBlogCacheDataMapper = SearchBlogCacheDataMapper()
        searchBlogCacheSource = DefaultSearchBlogCacheDataSource(
            searchDao = searchDao,
            searchBlogCacheDataMapper = searchBlogCacheDataMapper
        )
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun test_fetchBlogs_returns_all_saved_items() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.blogs

        // When
        searchBlogCacheSource.storeBlogs(blogList = expected)

        val result =
            searchBlogCacheSource.fetchBlogs(FakeDataGenerator.emptySearchQuery)

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_deleteAllBlogs_clears_the_cache() = runBlockingTest {
        // Given
        val expected = emptyList<BlogDataModel>()

        // When
        searchBlogCacheSource.storeBlogs(FakeDataGenerator.blogs)
        searchBlogCacheSource.deleteAllBlogs()

        val result =
            searchBlogCacheSource.fetchBlogs(FakeDataGenerator.emptySearchQuery)

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
