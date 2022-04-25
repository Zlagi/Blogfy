package com.zlagi.presentation.account.update

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.result.UpdatePasswordResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.account.update.UpdatePasswordUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordViewModel
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
class UpdatePasswordViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // systemUnderTest
    private lateinit var sut: UpdatePasswordViewModel

    @MockK
    private lateinit var updatePasswordUseCase: UpdatePasswordUseCase

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = UpdatePasswordViewModel(updatePasswordUseCase = updatePasswordUseCase)
    }

    @Test
    fun `when UpdatePasswordUseCase returns success then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult = UpdatePasswordResult(result = DataResult.Success(Unit))

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            val expectedViewEffect1 =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowToast

            val expectedViewEffect2 =
                UpdatePasswordContract.UpdatePasswordViewEffect.NavigateUp

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }

            sut.viewEffect.test {
                val actual1 = awaitItem()
                val actual2 = awaitItem()

                // Assertion
                Truth.assertThat(actual1).isEqualTo(expectedViewEffect1)
                Truth.assertThat(actual2).isEqualTo(expectedViewEffect2)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdatePasswordUseCase returns BadRequest then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult = UpdatePasswordResult(result = DataResult.Error(NetworkException.BadRequest))

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            val expectedViewEffect =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowSnackBarError(R.string.invalid_credentials_message)

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

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
    fun `when UpdatePasswordUseCase returns NetworkUnavailable then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult = UpdatePasswordResult(result = DataResult.Error(NetworkException.NetworkUnavailable))

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            val expectedViewEffect =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

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
    fun `when UpdatePasswordUseCase returns Network then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult = UpdatePasswordResult(result = DataResult.Error(NetworkException.Network))

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            val expectedViewEffect =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowSnackBarError(R.string.server_unreachable_message)

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

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
    fun `when CancelUpdatePasswordButtonClicked then verify viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val expectedViewEffect =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowDiscardChangesDialog

            // When
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.CancelUpdatePasswordButtonClicked)

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
                UpdatePasswordContract.UpdatePasswordViewEffect.NavigateUp

            // When
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmDialogButtonClicked)

            sut.viewEffect.test {
                // Then
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewEffect)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdatePasswordUseCase returns InputTooShort then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult =
                UpdatePasswordResult(currentPasswordError = AuthError.InputTooShort)

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword,
                currentPasswordError = R.string.password_error_message
            )

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when UpdatePasswordUseCase returns UnmatchedPassword then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val updatePasswordResult =
                UpdatePasswordResult(confirmPasswordError = AuthError.UnmatchedPassword)

            coEvery {
                updatePasswordUseCase.invoke(
                    any(),
                    any(),
                    any()
                )
            } returns updatePasswordResult

            val expectedViewState = UpdatePasswordContract.UpdatePasswordViewState(
                currentPassword = FakeDataGenerator.oldPassword,
                newPassword = FakeDataGenerator.newPassword,
                confirmNewPassword = FakeDataGenerator.newPassword
            )

            val expectedViewEffect =
                UpdatePasswordContract.UpdatePasswordViewEffect.ShowSnackBarError(R.string.password_unmatched)

            // When
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.CurrentPasswordChanged(
                    FakeDataGenerator.oldPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.NewPasswordChanged(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(
                UpdatePasswordContract.UpdatePasswordEvent.ConfirmNewPassword(
                    FakeDataGenerator.newPassword
                )
            )
            sut.setEvent(UpdatePasswordContract.UpdatePasswordEvent.ConfirmUpdatePasswordButtonClicked)

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
