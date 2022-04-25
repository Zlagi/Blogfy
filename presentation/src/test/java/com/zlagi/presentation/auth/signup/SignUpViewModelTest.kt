package com.zlagi.presentation.auth.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.result.SignUpResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.auth.signup.SignUpUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator
import com.zlagi.presentation.viewmodel.auth.signup.SignUpContract
import com.zlagi.presentation.viewmodel.auth.signup.SignUpViewModel
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
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var signUpUseCase: SignUpUseCase

    // systemUnderTest
    private lateinit var sut: SignUpViewModel

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = SignUpViewModel(
            signUpUseCase = signUpUseCase
        )
    }

    @Test
    fun `when SignUpUseCase returns success then verify viewState and viewEffect`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(result = DataResult.Success(Unit))

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                username = FakeDataGenerator.correctUsername
            )

            val expectedViewEffect = SignUpContract.SignUpViewEffect.NavigateToFeed

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(FakeDataGenerator.correctEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(FakeDataGenerator.correctUsername))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

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
    fun `when SignUpUseCase returns EmptyField emailError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(emailError = AuthError.EmptyField)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = "",
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                emailError = R.string.empty_field_message
            )

            // When
            sut.setEvent(
                SignUpContract.SignUpEvent.EmailChanged(
                    email = ""
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.UsernameChanged(
                    username = FakeDataGenerator.correctUsername
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.PasswordChanged(
                    password = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.ConfirmPasswordChanged(
                    confirmPassword = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignUpUseCase returns InvalidEmail emailError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(emailError = AuthError.InvalidEmail)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.incorrectEmail,
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                emailError = R.string.email_error_message
            )

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(FakeDataGenerator.incorrectEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(FakeDataGenerator.correctUsername))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns EmptyField usernameError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(usernameError = AuthError.EmptyField)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = "",
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                usernameError = R.string.empty_field_message
            )

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(FakeDataGenerator.correctEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(""))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns InputTooShort usernameError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(usernameError = AuthError.InputTooShort)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.incorrectUsername2,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                usernameError = R.string.username_error_message2
            )

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(FakeDataGenerator.correctEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(FakeDataGenerator.incorrectUsername2))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns InvalidUsername usernameError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(usernameError = AuthError.InvalidUsername)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.incorrectUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                usernameError = R.string.username_error_message
            )

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(FakeDataGenerator.correctEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(FakeDataGenerator.incorrectUsername))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns EmptyField passwordError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(passwordError = AuthError.EmptyField)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.correctUsername,
                password = "",
                confirmPassword = FakeDataGenerator.correctPassword,
                passwordError = R.string.empty_field_message
            )

            // When
            sut.setEvent(
                SignUpContract.SignUpEvent.EmailChanged(
                    email = FakeDataGenerator.correctEmail
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.UsernameChanged(
                    username = FakeDataGenerator.correctUsername
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(password = ""))
            sut.setEvent(
                SignUpContract.SignUpEvent.ConfirmPasswordChanged(
                    confirmPassword = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns InputTooShort passwordError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(passwordError = AuthError.InputTooShort)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.incorrectPassword,
                confirmPassword = FakeDataGenerator.correctPassword,
                passwordError = R.string.password_error_message
            )

            // When
            sut.setEvent(
                SignUpContract.SignUpEvent.EmailChanged(
                    email = FakeDataGenerator.correctEmail
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.UsernameChanged(
                    username = FakeDataGenerator.correctUsername
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.PasswordChanged(
                    password = FakeDataGenerator.incorrectPassword
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.ConfirmPasswordChanged(
                    confirmPassword = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns EmptyField confirmPasswordError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(confirmPasswordError = AuthError.EmptyField)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = "",
                confirmPasswordError = R.string.empty_field_message
            )

            // When
            sut.setEvent(SignUpContract.SignUpEvent.EmailChanged(email = FakeDataGenerator.correctEmail))
            sut.setEvent(SignUpContract.SignUpEvent.UsernameChanged(username = FakeDataGenerator.correctUsername))
            sut.setEvent(SignUpContract.SignUpEvent.PasswordChanged(password = FakeDataGenerator.correctPassword))
            sut.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(confirmPassword = ""))
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns InvalidPassword confirmPasswordError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(confirmPasswordError = AuthError.InputTooShort)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.incorrectPassword,
                confirmPasswordError = R.string.password_error_message
            )

            // When
            sut.setEvent(
                SignUpContract.SignUpEvent.EmailChanged(
                    email = FakeDataGenerator.correctEmail
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.UsernameChanged(
                    username = FakeDataGenerator.correctUsername
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.PasswordChanged(
                    password = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.ConfirmPasswordChanged(
                    confirmPassword = FakeDataGenerator.incorrectPassword
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }

    @Test
    fun `when SignInUseCase returns UnmatchedPassword confirmPasswordError then verify viewState`() =
        testCoroutineRule.runBlockingTest {
            // Given
            val result = SignUpResult(confirmPasswordError = AuthError.UnmatchedPassword)

            coEvery { signUpUseCase.invoke(any(), any(), any(), any()) } returns result

            val expectedViewState = SignUpContract.SignUpViewState(
                email = FakeDataGenerator.correctEmail,
                username = FakeDataGenerator.correctUsername,
                password = FakeDataGenerator.correctPassword,
                confirmPassword = FakeDataGenerator.incorrectPassword,
                confirmPasswordError = R.string.password_unmatched
            )

            // When
            sut.setEvent(
                SignUpContract.SignUpEvent.EmailChanged(
                    email = FakeDataGenerator.correctEmail
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.UsernameChanged(
                    username = FakeDataGenerator.correctUsername
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.PasswordChanged(
                    password = FakeDataGenerator.correctPassword
                )
            )
            sut.setEvent(
                SignUpContract.SignUpEvent.ConfirmPasswordChanged(
                    confirmPassword = FakeDataGenerator.incorrectPassword
                )
            )
            sut.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)

            // Then
            sut.viewState.test {
                val actual = awaitItem()

                // Assertion
                Truth.assertThat(actual).isEqualTo(expectedViewState)
                expectNoEvents()
            }
        }
}
