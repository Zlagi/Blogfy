package com.zlagi.presentation.account.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.taskmanager.TaskManager
import com.zlagi.domain.usecase.account.delete.DeleteAccountUseCase
import com.zlagi.domain.usecase.account.detail.GetAccountUseCase
import com.zlagi.domain.usecase.account.detail.SyncAccountUseCase
import com.zlagi.domain.usecase.auth.deletetokens.DeleteTokensUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.DeleteAllSearchSuggestionsUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator.account
import com.zlagi.presentation.mapper.AccountDomainPresentationMapper
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailViewModel
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
class AccountDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: AccountDetailViewModel

    @MockK
    private lateinit var syncAccountUseCase: SyncAccountUseCase

    @MockK
    private lateinit var getAccountUseCase: GetAccountUseCase

    @MockK
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @MockK
    private lateinit var deleteTokensUseCase: DeleteTokensUseCase

    @MockK
    private lateinit var taskManager: TaskManager

    @MockK
    private lateinit var deleteAllSearchSuggestionsUseCase: DeleteAllSearchSuggestionsUseCase

    private lateinit var accountDomainPresentationMapper: AccountDomainPresentationMapper

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        accountDomainPresentationMapper = AccountDomainPresentationMapper()

        sut = AccountDetailViewModel(
            syncAccountUseCase = syncAccountUseCase,
            getAccountUseCase = getAccountUseCase,
            taskManager = taskManager,
            deleteTokensUseCase = deleteTokensUseCase,
            deleteAllSearchSuggestionsUseCase = deleteAllSearchSuggestionsUseCase,
            accountDomainPresentationMapper = accountDomainPresentationMapper,
            deleteAccountUseCase = deleteAccountUseCase
        )
    }

    @Test
    fun `when SyncAccountUseCase returns success then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val syncAccountResult = DataResult.Success(Unit)
            val getAccountResult = account

            coEvery { syncAccountUseCase.invoke() } returns syncAccountResult
            coEvery { getAccountUseCase.invoke() } returns getAccountResult

            val expectedViewState = AccountDetailContract.AccountDetailViewState(
                loading = false,
                account = accountDomainPresentationMapper.from(account)
            )

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.Initialization)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual.loading).isEqualTo(expectedViewState.loading)
                Truth.assertThat(actual.account?.pk).isEqualTo(expectedViewState.account?.pk)
                Truth.assertThat(actual.account?.email).isEqualTo(expectedViewState.account?.email)
                Truth.assertThat(actual.account?.username)
                    .isEqualTo(expectedViewState.account?.username)
                expectNoEvents()
            }
        }

    @Test
    fun `when SyncAccountUseCase returns NetworkUnavailable then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getAccountResult = account

            coEvery { syncAccountUseCase.invoke() } returns DataResult.Error(NetworkException.NetworkUnavailable)
            coEvery { getAccountUseCase.invoke() } returns getAccountResult

            val expectedViewEffect =
                AccountDetailContract.AccountDetailViewEffect.ShowSnackBarError(
                    R.string.network_unavailable_message
                )

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.Initialization)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when SyncAccountUseCase returns Network then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val getAccountResult = account

            coEvery { syncAccountUseCase.invoke() } returns DataResult.Error(NetworkException.Network)
            coEvery { getAccountUseCase.invoke() } returns getAccountResult

            val expectedViewEffect =
                AccountDetailContract.AccountDetailViewEffect.ShowSnackBarError(
                    R.string.server_unreachable_message
                )

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.Initialization)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdatePasswordButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                AccountDetailContract.AccountDetailViewEffect.NavigateToUpdatePassword

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.UpdatePasswordButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignOutButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                AccountDetailContract.AccountDetailViewEffect.ShowDiscardChangesDialog

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.SignOutButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when ConfirmDialogButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                AccountDetailContract.AccountDetailViewEffect.NavigateToAuth

            // When
            sut.setEvent(AccountDetailContract.AccountDetailEvent.ConfirmDialogButtonClicked)

            // Then
            sut.viewEffect.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }
}