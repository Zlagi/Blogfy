package com.zlagi.data.repository

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.fakes.source.cache.FakeFeedCacheDataSource
import com.zlagi.data.fakes.source.network.FakeBlogNetworkDataSource
import com.zlagi.data.mapper.BlogDataDomainMapper
import com.zlagi.data.mapper.PaginationDataDomainMapper
import com.zlagi.data.repository.feed.DefaultFeedRepository
import com.zlagi.domain.repository.feed.FeedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@MediumTest
class FeedRepositoryTest {

    private lateinit var sut: FeedRepository
    private lateinit var blogDataDomainMapper: BlogDataDomainMapper
    private lateinit var paginationDataDomainMapper: PaginationDataDomainMapper

    @Before
    fun setup() {
        blogDataDomainMapper = BlogDataDomainMapper()
        paginationDataDomainMapper = PaginationDataDomainMapper()

        // systemUnderTest
        sut = DefaultFeedRepository(
            blogNetworkDataSource = FakeBlogNetworkDataSource(
                paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)

            ),
            feedCacheDataSource = FakeFeedCacheDataSource(),
            blogDataDomainMapper = blogDataDomainMapper,
            paginationDataDomainMapper = paginationDataDomainMapper
        )
    }

    @Test
    fun `requestMoreBlogs() returns blogs when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected =
            blogDataDomainMapper.fromList(FakeDataGenerator.paginatedBlogsDataModel.results)

        // When
        val result =
            sut.requestMoreBlogs(
                searchQuery = FakeDataGenerator.emptySearchQuery,
                page = FakeDataGenerator.page,
                pageSize = FakeDataGenerator.pageSize
            )

        // Then
        val actual = (result as DataResult.Success).data.results

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `requestMoreBlogs() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Error(NetworkException.NetworkUnavailable),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result =
                sut.requestMoreBlogs(
                    searchQuery = FakeDataGenerator.emptySearchQuery,
                    page = FakeDataGenerator.page,
                    pageSize = FakeDataGenerator.pageSize
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `requestMoreBlogs() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Error(NetworkException.Network),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.Network

            // When
            val result =
                sut.requestMoreBlogs(
                    searchQuery = FakeDataGenerator.emptySearchQuery,
                    page = FakeDataGenerator.page,
                    pageSize = FakeDataGenerator.pageSize
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `requestMoreBlogs() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Error(NetworkException.NotAuthorized),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result =
                sut.requestMoreBlogs(
                    searchQuery = FakeDataGenerator.emptySearchQuery,
                    page = FakeDataGenerator.page,
                    pageSize = FakeDataGenerator.pageSize
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @Test
    fun `getBlog() returns blog from CacheDataSource`() = runBlockingTest {
        // Given
        val expected = blogDataDomainMapper.from(FakeDataGenerator.blogCreated)

        // When
        val result = sut.getBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        result.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @Test
    fun `getBlogs() returns blogs from CacheDataSource`() = runBlockingTest {
        // Given
        val expected =
            blogDataDomainMapper.fromList(FakeDataGenerator.paginatedBlogsDataModel.results)

        // When
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
    fun `createBlog() returns blog when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = FakeDataGenerator.created

        // When
        val result =
            sut.createBlog(
                blogTitle = FakeDataGenerator.blogCreated.title,
                blogDescription = FakeDataGenerator.blogCreated.description,
                creationTime = FakeDataGenerator.blogCreated.created
            )

        // Then
        val actual = (result as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `createBlog() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Error(NetworkException.NetworkUnavailable),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result =
                sut.createBlog(
                    blogTitle = FakeDataGenerator.blogCreated.title,
                    blogDescription = FakeDataGenerator.blogCreated.description,
                    creationTime = FakeDataGenerator.blogCreated.created
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `createBlog() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Error(NetworkException.Network),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.Network

            // When
            val result =
                sut.createBlog(
                    blogTitle = FakeDataGenerator.blogCreated.title,
                    blogDescription = FakeDataGenerator.blogCreated.description,
                    creationTime = FakeDataGenerator.blogCreated.created
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `createBlog() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Error(NetworkException.NotAuthorized),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result =
                sut.createBlog(
                    blogTitle = FakeDataGenerator.blogCreated.title,
                    blogDescription = FakeDataGenerator.blogCreated.description,
                    creationTime = FakeDataGenerator.blogCreated.created
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updateBlog() returns blog when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = FakeDataGenerator.updated

        // When
        val result =
            sut.updateBlog(
                blogPk = FakeDataGenerator.blogUpdated.pk,
                blogTitle = FakeDataGenerator.blogUpdated.title,
                blogDescription = FakeDataGenerator.blogUpdated.description,
                updateTime = FakeDataGenerator.blogUpdated.updated
            )

        // Then
        val actual = (result as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updateBlog() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Error(NetworkException.NetworkUnavailable),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result =
                sut.updateBlog(
                    blogPk = FakeDataGenerator.blogUpdated.pk,
                    blogTitle = FakeDataGenerator.blogUpdated.title,
                    blogDescription = FakeDataGenerator.blogUpdated.description,
                    updateTime = FakeDataGenerator.blogUpdated.updated
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updateBlog() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Error(NetworkException.Network),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.Network

            // When
            val result =
                sut.updateBlog(
                    blogPk = FakeDataGenerator.blogUpdated.pk,
                    blogTitle = FakeDataGenerator.blogUpdated.title,
                    blogDescription = FakeDataGenerator.blogUpdated.description,
                    updateTime = FakeDataGenerator.blogUpdated.updated
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updateBlog() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Error(NetworkException.NotAuthorized),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result =
                sut.updateBlog(
                    blogPk = FakeDataGenerator.blogUpdated.pk,
                    blogTitle = FakeDataGenerator.blogUpdated.title,
                    blogDescription = FakeDataGenerator.blogUpdated.description,
                    updateTime = FakeDataGenerator.blogUpdated.updated
                )

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `deleteBlog() returns success when NetworkDataSource returns success`() = runBlocking {
        // Given
        val expected = FakeDataGenerator.deleted

        // When
        val result = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        val actual = (result as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `deleteBlog() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Error(NetworkException.NetworkUnavailable),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `deleteBlog() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Error(NetworkException.Network),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.Network

            // When
            val result = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `deleteBlog() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Error(NetworkException.NotAuthorized),
                    checkedAuthor = DataResult.Success(Constants.HAVE_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns success when NetworkDataSource returns HAVE_PERMISSION`() =
        runBlocking {
            // Given
            val expected = Constants.HAVE_PERMISSION

            // When
            val result = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Success).data

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns success when NetworkDataSource returns HAVE_NO_PERMISSION`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Error(NetworkException.NotAuthorized),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Success(Constants.HAVE_NO_PERMISSION)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = Constants.HAVE_NO_PERMISSION

            // When
            val result = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Success).data

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns error when NetworkDataSource throws NetworkUnavailable exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Error(NetworkException.NetworkUnavailable)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val result = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns error when NetworkDataSource throws Network exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Error(NetworkException.Network)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.Network

            // When
            val result = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns error when NetworkDataSource throws NotAuthorized exception`() =
        runBlocking {
            // Given
            sut = DefaultFeedRepository(
                blogNetworkDataSource = FakeBlogNetworkDataSource(
                    paginatedBlogs = DataResult.Success(FakeDataGenerator.paginatedBlogsDataModel),
                    createdBlog = DataResult.Success(FakeDataGenerator.blogCreated),
                    updatedBlog = DataResult.Success(FakeDataGenerator.blogUpdated),
                    deletedBlog = DataResult.Success(FakeDataGenerator.deleted),
                    checkedAuthor = DataResult.Error(NetworkException.NotAuthorized)
                ),
                feedCacheDataSource = FakeFeedCacheDataSource(),
                blogDataDomainMapper = blogDataDomainMapper,
                paginationDataDomainMapper = paginationDataDomainMapper
            )
            val expected = NetworkException.NotAuthorized

            // When
            val result = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (result as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }
}
