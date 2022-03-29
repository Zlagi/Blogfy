package com.zlagi.presentation.blog.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.feed.GetFeedUseCase
import com.zlagi.domain.usecase.blog.feed.RequestMoreBlogsUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract
import com.zlagi.presentation.viewmodel.blog.feed.FeedViewModel
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
class FeedViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: FeedViewModel

    private lateinit var blogDomainPresentationMapper: BlogDomainPresentationMapper

    @MockK
    private lateinit var getFeedUseCase: GetFeedUseCase

    @MockK
    private lateinit var requestMoreBlogsUseCase: RequestMoreBlogsUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        blogDomainPresentationMapper = BlogDomainPresentationMapper()

        sut = FeedViewModel(
            getFeedUseCase = getFeedUseCase,
            requestMoreBlogsUseCase = requestMoreBlogsUseCase,
            blogDomainPresentationMapper = blogDomainPresentationMapper
        )
    }

    @Test
    fun `when RequestMoreBlogsUseCase returns success then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Success(FakeDataGenerator.paginatedBlogsDomainModel)
            coEvery { getFeedUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewState = FeedContract.FeedViewState(
                results = blogDomainPresentationMapper.fromList(FakeDataGenerator.blogs),
            )

            // When
            sut.setEvent(FeedContract.FeedEvent.Initialization)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when RequestMoreBlogsUseCase returns NetworkUnavailable then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Error(NetworkException.NetworkUnavailable)
            coEvery { getFeedUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewEffect = FeedContract.FeedViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(FeedContract.FeedEvent.Initialization)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when RequestMoreBlogsUseCase returns Network then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Error(NetworkException.Network)
            coEvery { getFeedUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewEffect = FeedContract.FeedViewEffect.ShowSnackBarError(R.string.server_unreachable_message)

            // When
            sut.setEvent(FeedContract.FeedEvent.Initialization)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }


    @Test
    fun `when CreateBlogButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expected = FeedContract.FeedViewEffect.Navigate(null)

            // When
            sut.setEvent(FeedContract.FeedEvent.CreateBlogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expected)
                expectNoEvents()
            }
        }

    @Test
    fun `when BlogItemClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect = FeedContract.FeedViewEffect.Navigate(blogPk = FakeDataGenerator.blogPk)

            // When
            sut.setEvent(FeedContract.FeedEvent.BlogItemClicked(blogPk = FakeDataGenerator.blogPk))

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }
}
