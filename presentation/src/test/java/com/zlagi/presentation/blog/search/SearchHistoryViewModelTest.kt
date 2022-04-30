package com.zlagi.presentation.blog.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.CacheException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.search.history.ClearHistoryUseCase
import com.zlagi.domain.usecase.blog.search.history.DeleteQueryUseCase
import com.zlagi.domain.usecase.blog.search.history.GetHistoryUseCase
import com.zlagi.domain.usecase.blog.search.history.SaveQueryUseCase
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.mapper.HistoryDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.search.historyview.SearchHistoryContract
import com.zlagi.presentation.viewmodel.blog.search.historyview.SearchHistoryViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@MediumTest
class SearchHistoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: SearchHistoryViewModel

    @MockK
    private lateinit var getHistoryUseCase: GetHistoryUseCase

    @MockK
    private lateinit var saveQueryUseCase: SaveQueryUseCase

    @MockK
    private lateinit var deleteQueryUseCase: DeleteQueryUseCase

    @MockK
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase

    private lateinit var historyDomainPresentationMapper: HistoryDomainPresentationMapper

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        historyDomainPresentationMapper = HistoryDomainPresentationMapper()

        sut = SearchHistoryViewModel(
            saveQueryUseCase = saveQueryUseCase,
            deleteQueryUseCase = deleteQueryUseCase,
            getHistoryUseCase = getHistoryUseCase,
            clearHistoryUseCase = clearHistoryUseCase,
            historyDomainPresentationMapper = historyDomainPresentationMapper
        )
    }

    @Test
    fun `when GetHistoryUseCase returns data then verify viewState`() =
        runBlockingTest {
            // Given
            val result = DataResult.Success(FakeDataGenerator.history)

            coEvery { getHistoryUseCase.invoke() } returns result

            val expectedViewState = SearchHistoryContract.SearchHistoryViewState(
                data = historyDomainPresentationMapper.fromList(
                    FakeDataGenerator.history
                ),
                emptyHistory = false
            )

            // When
            sut.setEvent(SearchHistoryContract.SearchHistoryEvent.LoadHistory)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }


    @Test
    fun `when GetHistoryUseCase returns empty then verify viewState`() =
        runBlockingTest {
            // Given
            val result = CacheException.NoResults

            coEvery { getHistoryUseCase.invoke() } returns DataResult.Error(result)

            val expectedViewState = SearchHistoryContract.SearchHistoryViewState(
                data = emptyList(),
                emptyHistory = true
            )

            // When
            sut.setEvent(SearchHistoryContract.SearchHistoryEvent.LoadHistory)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }
}
