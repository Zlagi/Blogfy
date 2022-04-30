package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.cache.FakeSearchBlogCacheDataSource
import com.zlagi.data.fakes.source.network.FakeBlogNetworkDataSource
import com.zlagi.data.mapper.BlogDataDomainMapper
import com.zlagi.data.mapper.PaginationDataDomainMapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.repository.search.DefaultSearchBlogRepository
import com.zlagi.domain.repository.search.SearchBlogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@MediumTest
class SearchBlogRepositoryTest {

    private lateinit var sut: SearchBlogRepository
    private lateinit var blogDataDomainMapper: BlogDataDomainMapper
    private lateinit var paginationDataDomainMapper: PaginationDataDomainMapper

    @Before
    fun setup() {
        blogDataDomainMapper = BlogDataDomainMapper()
        paginationDataDomainMapper = PaginationDataDomainMapper()

        // systemUnderTest
        sut = DefaultSearchBlogRepository(
            blogNetworkDataSource = FakeBlogNetworkDataSource(
                paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)

            ),
            searchBlogCacheDataSource = FakeSearchBlogCacheDataSource(),
            blogDataDomainMapper = blogDataDomainMapper,
            paginationDataDomainMapper = paginationDataDomainMapper
        )
    }

    @Test
    fun `test getBlogs() returns blogs from CacheDataSource`() = runBlockingTest {
        // Given
        val expected =
            blogDataDomainMapper.fromList(FakeDataGenerator.paginatedBlogsDataModel.results)

        // When
        sut.storeBlogs(blogList = expected)
        val result = sut.getBlogs(searchQuery = FakeDataGenerator.emptySearchQuery)

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test deleteAllBlogs() clears the CacheDataSource`() = runBlockingTest {
        // Given
        val expected = emptyList<BlogDataModel>()
        val data =
            blogDataDomainMapper.fromList(FakeDataGenerator.paginatedBlogsDataModel.results)

        // When
        sut.storeBlogs(blogList = data)
        sut.deleteAllBlogs()
        val result = sut.getBlogs(searchQuery = FakeDataGenerator.emptySearchQuery)

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
