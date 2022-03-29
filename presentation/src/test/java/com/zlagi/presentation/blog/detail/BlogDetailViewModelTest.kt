package com.zlagi.presentation.blog.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.checkauthor.CheckBlogAuthorUseCase
import com.zlagi.domain.usecase.blog.delete.DeleteBlogUseCase
import com.zlagi.domain.usecase.blog.detail.GetBlogUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@MediumTest
class BlogDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: BlogDetailViewModel

    @MockK
    private lateinit var getBlogUseCase: GetBlogUseCase

    @MockK
    private lateinit var checkBlogAuthorUseCase: CheckBlogAuthorUseCase

    @MockK
    private lateinit var deleteBlogUseCase: DeleteBlogUseCase

    private lateinit var blogDomainPresentationMapper: BlogDomainPresentationMapper

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        blogDomainPresentationMapper = BlogDomainPresentationMapper()

        sut = BlogDetailViewModel(
            getBlogUseCase = getBlogUseCase,
            checkBlogAuthorUseCase = checkBlogAuthorUseCase,
            deleteBlogUseCase = deleteBlogUseCase,
            blogDomainPresentationMapper = blogDomainPresentationMapper,
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun `when GetBlogUseCase returns blog then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.oldBlog

            coEvery { getBlogUseCase.invoke(any()) } returns result

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewState = BlogDetailContract.BlogDetailViewState(
                isAuthor = false,
                blog = blogDomainPresentationMapper.from(FakeDataGenerator.oldBlog),
            )

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.Initialization)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when CheckBlogAuthorUseCase returns true then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getBlogResult = FakeDataGenerator.oldBlog
            val checkBlogAuthorResult = DataResult.Success(true)

            coEvery { getBlogUseCase.invoke(any()) } returns getBlogResult
            coEvery { checkBlogAuthorUseCase.invoke(any()) } returns checkBlogAuthorResult

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewState = BlogDetailContract.BlogDetailViewState(
                isAuthor = true,
                blog = blogDomainPresentationMapper.from(FakeDataGenerator.oldBlog),
            )

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.Initialization)
            sut.setEvent(BlogDetailContract.BlogDetailEvent.CheckBlogAuthor)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdateBlogButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewEffect =
                BlogDetailContract.BlogDetailViewEffect.NavigateToUpdateBlog(FakeDataGenerator.oldBlog.pk)

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.UpdateBlogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when DeleteBlogButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewEffect =
                BlogDetailContract.BlogDetailViewEffect.ShowDeleteBlogDialog

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.DeleteBlogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when DeleteBlogUseCase returns success then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val deleteBlogResult = DataResult.Success("Deleted")

            coEvery { deleteBlogUseCase.invoke(any()) } returns deleteBlogResult

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewState = BlogDetailContract.BlogDetailViewState(
                isAuthor = false,
                blog = null,
            )

            val expectedViewEffect =
                BlogDetailContract.BlogDetailViewEffect.NavigateUp(true)

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.ConfirmDialogButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when DeleteBlogUseCase returns NetworkUnavailable then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            coEvery { deleteBlogUseCase.invoke(any()) } returns DataResult.Error(NetworkException.NetworkUnavailable)

            sut.blogPk = FakeDataGenerator.oldBlog.pk

            val expectedViewState = BlogDetailContract.BlogDetailViewState(
                isAuthor = false,
                blog = null,
            )

            val expectedViewEffect =
                BlogDetailContract.BlogDetailViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(BlogDetailContract.BlogDetailEvent.ConfirmDialogButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }
}