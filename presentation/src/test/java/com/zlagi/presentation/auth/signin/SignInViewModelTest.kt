package com.zlagi.presentation.auth.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.result.SignInResult
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.auth.signin.email.SignInUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.fakes.FakeDataGenerator.correctEmail
import com.zlagi.presentation.fakes.FakeDataGenerator.correctPassword
import com.zlagi.presentation.fakes.FakeDataGenerator.incorrectEmail
import com.zlagi.presentation.fakes.FakeDataGenerator.incorrectPassword
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract
import com.zlagi.presentation.viewmodel.auth.signin.SignInViewModel
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
class SignInViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var signInUseCase: SignInUseCase

    // systemUnderTest
    private lateinit var sut: SignInViewModel

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = SignInViewModel(
            signInUseCase = signInUseCase
        )
    }

    @Test
    fun `when SignInUseCase returns success then verify viewState and viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(result = DataResult.Success(Unit))

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = correctEmail,
            password = correctPassword
        )
        val expectedViewEffect = SignInContract.SignInViewEffect.NavigateToFeed

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(correctEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(correctPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

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
    fun `when SignUpTextViewClicked then verify viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val expectedViewEffect = SignInContract.SignInViewEffect.NavigateToSignUp

        // When
        sut.setEvent(SignInContract.SignInEvent.SignUpTextViewClicked)

        // Then
        sut.viewEffect.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expectedViewEffect)
            expectNoEvents()
        }
    }

    @Test
    fun `when SignInUseCase returns emailError then verify viewState`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(emailError = AuthError.InvalidEmail)

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = incorrectEmail,
            password = correctPassword,
            emailError = R.string.email_error_message
        )

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(incorrectEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(correctPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

        // Then
        sut.viewState.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expectedViewState)
            expectNoEvents()
        }
    }

    @Test
    fun `when SignInUseCase returns passwordError then verify viewState`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(passwordError = AuthError.InvalidPassword)

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = correctEmail,
            password = incorrectPassword,
            passwordError = R.string.password_error_message
        )

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(correctEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(incorrectPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

        // Then
        sut.viewState.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expectedViewState)
            expectNoEvents()
        }
    }

    @Test
    fun `when SignInUseCase returns NotAuthorized then verify viewState and viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(result = DataResult.Error(NetworkException.NotAuthorized))

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = correctEmail,
            password = correctPassword
        )

        val expectedViewEffect = SignInContract.SignInViewEffect.ShowSnackBarError(R.string.invalid_credentials_message)

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(correctEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(correctPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

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
    fun `when SignInUseCase returns NetworkUnavailable then verify viewState and viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(result = DataResult.Error(NetworkException.NetworkUnavailable))

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = correctEmail,
            password = correctPassword
        )

        val expectedViewEffect = SignInContract.SignInViewEffect.ShowSnackBarError(R.string.network_unavailable_message)

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(correctEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(correctPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

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
    fun `when SignInUseCase returns Network then verify viewState and viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val result = SignInResult(result = DataResult.Error(NetworkException.Network))

        coEvery { signInUseCase.invoke(any(), any()) } returns result

        val expectedViewState = SignInContract.SignInViewState(
            email = correctEmail,
            password = correctPassword
        )

        val expectedViewEffect = SignInContract.SignInViewEffect.ShowSnackBarError(R.string.server_unreachable_message)

        // When
        sut.setEvent(SignInContract.SignInEvent.EmailChanged(correctEmail))
        sut.setEvent(SignInContract.SignInEvent.PasswordChanged(correctPassword))
        sut.setEvent(SignInContract.SignInEvent.SignInButtonClicked)

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