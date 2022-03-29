package com.zlagi.network.source

import android.net.Uri
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.Constants
import com.zlagi.common.utils.Extensions
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.network.apiservice.BlogApiService
import com.zlagi.network.enqueueResponse
import com.zlagi.network.fakes.FakeConnectivityCheckReturnError
import com.zlagi.network.fakes.FakeConnectivityCheckReturnSuccess
import com.zlagi.network.fakes.FakeDataGenerator
import com.zlagi.network.mapper.BlogNetworkDataMapper
import com.zlagi.network.mapper.PaginationNetworkDataMapper
import com.zlagi.network.model.*
import io.mockk.MockKAnnotations
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@MediumTest
class DefaultBlogNetworkDataSourceTest {

    // systemUnderTest
    private lateinit var sut: BlogNetworkDataSource

    private val mockWebServer = MockWebServer()

    private lateinit var blogApiService: BlogApiService

    private lateinit var paginationNetworkDataMapper: PaginationNetworkDataMapper

    private lateinit var blogsNetworkDataMapper: BlogNetworkDataMapper

    private lateinit var exceptionMapper: ExceptionMapper

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(Uri::class)

        blogApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(Extensions.moshi))
            .build()
            .create(BlogApiService::class.java)

        paginationNetworkDataMapper = PaginationNetworkDataMapper()

        blogsNetworkDataMapper = BlogNetworkDataMapper()

        exceptionMapper = ExceptionMapper()

        sut = DefaultBlogNetworkDataSource(
            blogApiService = blogApiService,
            paginationNetworkDataMapper = paginationNetworkDataMapper,
            blogNetworkDataMapper = blogsNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnSuccess(),
            exceptionMapper = exceptionMapper
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getBlogs() returns blogs when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(FEED_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.blogs

        // When
        val response = sut.getBlogs(
            searchQuery = FakeDataGenerator.emptySearchQuery,
            page = FakeDataGenerator.page,
            pageSize = FakeDataGenerator.pageSize
        )

        // Then
        val actual = (response as DataResult.Success).data.results

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getBlogs() throws NetworkUnavailable exception when network is offline`() =
        runBlocking {
            // Given
            sut = DefaultBlogNetworkDataSource(
                blogApiService = blogApiService,
                paginationNetworkDataMapper = paginationNetworkDataMapper,
                blogNetworkDataMapper = blogsNetworkDataMapper,
                connectivityChecker = FakeConnectivityCheckReturnError(),
                exceptionMapper = exceptionMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val response = sut.getBlogs(
                searchQuery = FakeDataGenerator.emptySearchQuery,
                page = FakeDataGenerator.page,
                pageSize = FakeDataGenerator.pageSize
            )

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `getBlogs() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.getBlogs(
            searchQuery = FakeDataGenerator.emptySearchQuery,
            page = FakeDataGenerator.page,
            pageSize = FakeDataGenerator.pageSize
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun `getBlogs() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.getBlogs(
            searchQuery = FakeDataGenerator.emptySearchQuery,
            page = FakeDataGenerator.page,
            pageSize = FakeDataGenerator.pageSize
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `createBlog() returns Blog when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(CREATE_BLOG_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.blogCreated

        // When
        val response = sut.createBlog(
            blogTitle = FakeDataGenerator.blogCreated.title,
            blogDescription = FakeDataGenerator.blogCreated.description,
            creationTime = FakeDataGenerator.blogCreated.created
        )

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `createBlog() throws NetworkUnavailable exception when network is offline`() =
        runBlocking {
            // Given
            sut = DefaultBlogNetworkDataSource(
                blogApiService = blogApiService,
                paginationNetworkDataMapper = paginationNetworkDataMapper,
                blogNetworkDataMapper = blogsNetworkDataMapper,
                connectivityChecker = FakeConnectivityCheckReturnError(),
                exceptionMapper = exceptionMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val response = sut.createBlog(
                blogTitle = FakeDataGenerator.blogCreated.title,
                blogDescription = FakeDataGenerator.blogCreated.description,
                creationTime = FakeDataGenerator.blogCreated.created
            )

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }


    @Test
    fun `createBlog() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.createBlog(
            blogTitle = FakeDataGenerator.blogCreated.title,
            blogDescription = FakeDataGenerator.blogCreated.description,
            creationTime = FakeDataGenerator.blogCreated.created
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `createBlog() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.createBlog(
            blogTitle = FakeDataGenerator.blogCreated.title,
            blogDescription = FakeDataGenerator.blogCreated.description,
            creationTime = FakeDataGenerator.blogCreated.created
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updatedBlog() returns Blog when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(UPDATE_BLOG_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.blogUpdated

        // When
        val response = sut.updateBlog(
            blogPk = FakeDataGenerator.blogUpdated.pk,
            blogTitle = FakeDataGenerator.blogUpdated.title,
            blogDescription = FakeDataGenerator.blogUpdated.description,
            updateTime = FakeDataGenerator.blogUpdated.updated
        )

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updateBlog() throws NetworkUnavailable exception when network is offline`() =
        runBlocking {
            // Given
            sut = DefaultBlogNetworkDataSource(
                blogApiService = blogApiService,
                paginationNetworkDataMapper = paginationNetworkDataMapper,
                blogNetworkDataMapper = blogsNetworkDataMapper,
                connectivityChecker = FakeConnectivityCheckReturnError(),
                exceptionMapper = exceptionMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val response = sut.updateBlog(
                blogPk = FakeDataGenerator.blogUpdated.pk,
                blogTitle = FakeDataGenerator.blogUpdated.title,
                blogDescription = FakeDataGenerator.blogUpdated.description,
                updateTime = FakeDataGenerator.blogUpdated.updated
            )

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `updateBlog() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.updateBlog(
            blogPk = FakeDataGenerator.blogUpdated.pk,
            blogTitle = FakeDataGenerator.blogUpdated.title,
            blogDescription = FakeDataGenerator.blogUpdated.description,
            updateTime = FakeDataGenerator.blogUpdated.updated
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `updateBlog() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.updateBlog(
            blogPk = FakeDataGenerator.blogUpdated.pk,
            blogTitle = FakeDataGenerator.blogUpdated.title,
            blogDescription = FakeDataGenerator.blogUpdated.description,
            updateTime = FakeDataGenerator.blogUpdated.updated
        )

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `deleteBlog() returns success message when status is 200`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(BLOG_DELETED_RESPONSE_JSON, 200)
        val expected = FakeDataGenerator.deleted

        // When
        val response = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        val actual = (response as DataResult.Success).data

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `deleteBlog() throws NetworkUnavailable exception when network is offline`() = runBlocking {
        // Given
        sut = DefaultBlogNetworkDataSource(
            blogApiService = blogApiService,
            paginationNetworkDataMapper = paginationNetworkDataMapper,
            blogNetworkDataMapper = blogsNetworkDataMapper,
            connectivityChecker = FakeConnectivityCheckReturnError(),
            exceptionMapper = exceptionMapper
        )
        val expected = NetworkException.NetworkUnavailable

        // When
        val response = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `deleteBlog() throws Network exception when server is unreachable`() = runBlocking {
        // Given
        val expected = NetworkException.Network

        // When
        val response = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `deleteBlog() throws NotAuthorized exception when status is 401`() = runBlocking {
        // Given
        mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
        val expected = NetworkException.NotAuthorized

        // When
        val response = sut.deleteBlog(blogPk = FakeDataGenerator.blogPk)

        // Then
        val actual = (response as DataResult.Error).exception

        // Assertion
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `checkBlogAuthor() returns AUTHOR_HAVE_PERMISSION_RESPONSE message when status is 200`() =
        runBlocking {
            // Given
            mockWebServer.enqueueResponse(AUTHOR_HAVE_PERMISSION_RESPONSE, 200)
            val expected = Constants.HAVE_PERMISSION

            // When
            val response = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (response as DataResult.Success).data

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() returns AUTHOR_HAVE_NO_PERMISSION_RESPONSE message when status is 200`() =
        runBlocking {
            // Given
            mockWebServer.enqueueResponse(AUTHOR_HAVE_NO_PERMISSION_RESPONSE, 200)
            val expected = Constants.HAVE_NO_PERMISSION

            // When
            val response = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (response as DataResult.Success).data

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() throws NetworkUnavailable exception when network is offline`() =
        runBlocking {
            // Given
            sut = DefaultBlogNetworkDataSource(
                blogApiService = blogApiService,
                paginationNetworkDataMapper = paginationNetworkDataMapper,
                blogNetworkDataMapper = blogsNetworkDataMapper,
                connectivityChecker = FakeConnectivityCheckReturnError(),
                exceptionMapper = exceptionMapper
            )
            val expected = NetworkException.NetworkUnavailable

            // When
            val response = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() throws Network exception when server is unreachable`() =
        runBlocking {
            // Given
            val expected = NetworkException.Network

            // When
            val response = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }

    @Test
    fun `checkBlogAuthor() throws NotAuthorized exception when status is 401`() =
        runBlocking {
            // Given
            mockWebServer.enqueueResponse(EXPIRED_TOKEN_RESPONSE_JSON, 401)
            val expected = NetworkException.NotAuthorized

            // When
            val response = sut.checkBlogAuthor(blogPk = FakeDataGenerator.blogPk)

            // Then
            val actual = (response as DataResult.Error).exception

            // Assertion
            Truth.assertThat(actual).isEqualTo(expected)
        }
}
