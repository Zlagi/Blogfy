package com.zlagi.presentation.blog.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.domain.usecase.blog.search.GetSearchUseCase
import com.zlagi.domain.usecase.blog.search.RequestMoreBlogsUseCase
import com.zlagi.domain.usecase.blog.search.history.SaveQueryUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.search.resultview.SearchResultContract
import com.zlagi.presentation.viewmodel.blog.search.resultview.SearchResultViewModel
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
class SearchResultViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: SearchResultViewModel

    @MockK
    private lateinit var getSearchUseCase: GetSearchUseCase

    @MockK
    private lateinit var requestMoreBlogsUseCase: RequestMoreBlogsUseCase

    @MockK
    private lateinit var saveQueryUseCase: SaveQueryUseCase

    private lateinit var blogDomainPresentationMapper: BlogDomainPresentationMapper

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        blogDomainPresentationMapper = BlogDomainPresentationMapper()

        sut = SearchResultViewModel(
            getSearchUseCase = getSearchUseCase,
            requestMoreBlogsUseCase = requestMoreBlogsUseCase,
            saveQueryUseCase = saveQueryUseCase,
            blogDomainPresentationMapper = blogDomainPresentationMapper,
        )
    }

    @Test
    fun `when RequestMoreBlogsUseCase returns data then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.blogs

            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Success(FakeDataGenerator.paginatedBlogsDomainModel)
            coEvery { getSearchUseCase.invoke(any()) } returns result

            val expectedViewState = SearchResultContract.SearchResultViewState(
                blogs = blogDomainPresentationMapper.fromList(FakeDataGenerator.blogs),
                query = FakeDataGenerator.query,
            )

            // When
            sut.setEvent(
                SearchResultContract.SearchResultEvent.ExecuteSearch(
                    init = true,
                    query = FakeDataGenerator.query
                )
            )

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }


    @Test
    fun `when RequestMoreBlogsUseCase returns NoResults then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = emptyList<BlogDomainModel>()

            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Error(NetworkException.NoResults)
            coEvery { getSearchUseCase.invoke(any()) } returns result

            val expectedViewState = SearchResultContract.SearchResultViewState(
                blogs = emptyList(),
                showEmptyBlogs = true,
                query = FakeDataGenerator.query,
            )

            // When
            sut.setEvent(
                SearchResultContract.SearchResultEvent.ExecuteSearch(
                    init = true,
                    query = FakeDataGenerator.query
                )
            )

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
            val result = FakeDataGenerator.blogs

            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Error(NetworkException.NetworkUnavailable)
            coEvery { getSearchUseCase.invoke(any()) } returns result

            val expectedViewEffect =
                SearchResultContract.SearchResultViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(
                SearchResultContract.SearchResultEvent.ExecuteSearch(
                    init = true,
                    query = FakeDataGenerator.query,
                )
            )

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
            val result = FakeDataGenerator.blogs

            coEvery {
                requestMoreBlogsUseCase.invoke(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns DataResult.Error(NetworkException.Network)
            coEvery { getSearchUseCase.invoke(any()) } returns result

            val expectedViewEffect =
                SearchResultContract.SearchResultViewEffect.ShowSnackBarError(R.string.server_unreachable_message)

            // When
            sut.setEvent(
                SearchResultContract.SearchResultEvent.ExecuteSearch(
                    init = true,
                    query = FakeDataGenerator.query
                )
            )

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }
}
