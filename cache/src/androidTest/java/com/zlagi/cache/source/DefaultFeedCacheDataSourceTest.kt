package com.zlagi.cache.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.cache.database.feed.FeedDao
import com.zlagi.cache.database.BlogfyDatabase
import com.zlagi.cache.fakes.FakeDataGenerator
import com.zlagi.cache.mapper.FeedBlogCacheDataMapper
import com.zlagi.cache.source.feed.DefaultFeedCacheDataSource
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.source.cache.feed.FeedCacheDataSource
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
class DefaultFeedCacheDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var feedCacheDataSource: FeedCacheDataSource

    @Inject
    @Named("test_db")
    lateinit var database: BlogfyDatabase

    private lateinit var feedDao: FeedDao

    private lateinit var feedBlogCacheDataMapper: FeedBlogCacheDataMapper

    @Before
    fun setup() {
        hiltRule.inject()
        feedDao = database.feedDao()
        feedBlogCacheDataMapper = FeedBlogCacheDataMapper()
        feedCacheDataSource = DefaultFeedCacheDataSource(feedDao, feedBlogCacheDataMapper)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun storeBlogs_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.blogs

        // When
        feedCacheDataSource.storeBlogs(expected)

        // Then
        feedCacheDataSource.fetchBlogs(FakeDataGenerator.emptySearchQuery).test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun storeBlog_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.blogCreated

        // When
        feedCacheDataSource.storeBlog(expected)

        // Then
        feedCacheDataSource.fetchBlog(expected.pk).test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateBlog_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.blogUpdated

        // When
        feedCacheDataSource.storeBlog(FakeDataGenerator.blogUpdated)
        feedCacheDataSource.updateBlog(expected)

        // Then
        feedCacheDataSource.fetchBlog(FakeDataGenerator.blogUpdated.pk).test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteBlog_success() = runBlockingTest {
        // Given
        val expected = FakeDataGenerator.blogs[2]

        // When
        feedCacheDataSource.storeBlogs(FakeDataGenerator.blogs)
        feedCacheDataSource.deleteBlog(FakeDataGenerator.blogs[0].pk)
        feedCacheDataSource.deleteBlog(FakeDataGenerator.blogs[1].pk)

        // Then
        feedCacheDataSource.fetchBlog(FakeDataGenerator.blogs[2].pk).test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteAllBlogs_success() = runBlockingTest {
        // Given
        val expected = emptyList<BlogDataModel>()

        // When
        feedCacheDataSource.storeBlogs(FakeDataGenerator.blogs)
        feedCacheDataSource.deleteAllBlogs()

        // Then
        feedCacheDataSource.fetchBlogs(FakeDataGenerator.emptySearchQuery).test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
