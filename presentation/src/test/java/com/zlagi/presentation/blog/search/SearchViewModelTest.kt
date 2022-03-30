package com.zlagi.presentation.blog.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.search.GetBlogsByUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.DeleteAllSearchSuggestionsUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.DeleteSearchSuggestionUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.GetSearchSuggestionsUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.StoreSearchSuggestionUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.fakes.FakeDataGenerator.searchSuggestionUpdated
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.mapper.SearchSuggestionDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.search.SearchBlogViewModel
import com.zlagi.presentation.viewmodel.blog.search.SearchContract
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
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: SearchBlogViewModel

    @MockK
    private lateinit var getBlogsByUseCase: GetBlogsByUseCase

    @MockK
    private lateinit var requestMoreBlogsUseCase: com.zlagi.domain.usecase.blog.search.RequestMoreBlogsUseCase

    @MockK
    private lateinit var storeSearchSuggestionUseCase: StoreSearchSuggestionUseCase

    @MockK
    private lateinit var deleteSearchSuggestionUseCase: DeleteSearchSuggestionUseCase

    @MockK
    private lateinit var deleteAllSearchSuggestionsUseCase: DeleteAllSearchSuggestionsUseCase

    @MockK
    private lateinit var getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase

    private lateinit var blogDomainPresentationMapper: BlogDomainPresentationMapper

    private lateinit var searchDomainPresentationMapper: SearchSuggestionDomainPresentationMapper

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        blogDomainPresentationMapper = BlogDomainPresentationMapper()

        searchDomainPresentationMapper = SearchSuggestionDomainPresentationMapper()

        sut = SearchBlogViewModel(
            getBlogsByUseCase = getBlogsByUseCase,
            requestMoreBlogsUseCase = requestMoreBlogsUseCase,
            storeSearchSuggestionUseCase = storeSearchSuggestionUseCase,
            deleteSearchSuggestionUseCase = deleteSearchSuggestionUseCase,
            deleteAllSearchSuggestionsUseCase = deleteAllSearchSuggestionsUseCase,
            getSearchSuggestionsUseCase = getSearchSuggestionsUseCase,
            blogDomainPresentationMapper = blogDomainPresentationMapper,
            searchDomainPresentationMapper = searchDomainPresentationMapper
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
            coEvery { getBlogsByUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewState = SearchContract.SearchViewState(
                blogs = blogDomainPresentationMapper.fromList(FakeDataGenerator.blogs),
                query = "something",
                searchSuggestionViewExpanded = false,
                searchSuggestionViewCollapsed = false,
                searchBlogResultView = true,
                searchTextFocused = true
            )

            // When
            sut.setEvent(SearchContract.SearchEvent.NewSearch("something"))

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
            coEvery { getBlogsByUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewEffect = SearchContract.SearchViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(SearchContract.SearchEvent.NewSearch("something"))

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
            coEvery { getBlogsByUseCase.invoke(any()) } returns FakeDataGenerator.blogs

            val expectedViewEffect = SearchContract.SearchViewEffect.ShowSnackBarError(R.string.server_unreachable_message)

            // When
            sut.setEvent(SearchContract.SearchEvent.NewSearch("something"))

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when DeleteSearchSuggestionButtonClicked then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.searchSuggestionList

            coEvery { getSearchSuggestionsUseCase.invoke() } returns result

            coEvery { deleteSearchSuggestionUseCase.invoke(FakeDataGenerator.searchSuggestions[0].pk) } returns Unit

            coEvery { deleteSearchSuggestionUseCase.invoke(FakeDataGenerator.searchSuggestions[1].pk) } returns Unit

            val expectedViewState = SearchContract.SearchViewState(
                searchSuggestions = searchDomainPresentationMapper.fromList(searchSuggestionUpdated)
            )

            // When
            sut.setEvent(SearchContract.SearchEvent.Initialization)
            sut.setEvent(
                SearchContract.SearchEvent.DeleteSearchSuggestionButtonClicked(
                    FakeDataGenerator.searchSuggestions[0].pk
                )
            )
            sut.setEvent(
                SearchContract.SearchEvent.DeleteSearchSuggestionButtonClicked(
                    FakeDataGenerator.searchSuggestions[1].pk
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
    fun `when DeleteAllSearchSuggestionsButtonClicked then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.searchSuggestionList

            coEvery { getSearchSuggestionsUseCase.invoke() } returns result

            coEvery { deleteAllSearchSuggestionsUseCase.invoke() } returns Unit

            val expectedViewState = SearchContract.SearchViewState(
                searchSuggestions = emptyList()
            )

            // When
            sut.setEvent(SearchContract.SearchEvent.Initialization)
            sut.setEvent(SearchContract.SearchEvent.DeleteAllSearchSuggestionsButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when GetSearchSuggestionsUseCase returns searchSuggestionList then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = FakeDataGenerator.searchSuggestionList

            coEvery { getSearchSuggestionsUseCase.invoke() } returns result

            val expectedViewState = SearchContract.SearchViewState(
                searchSuggestions = searchDomainPresentationMapper.fromList(FakeDataGenerator.searchSuggestions.asReversed())
            )

            // When
            sut.setEvent(SearchContract.SearchEvent.Initialization)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }
}