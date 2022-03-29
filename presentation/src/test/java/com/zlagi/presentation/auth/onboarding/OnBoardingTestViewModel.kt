package com.zlagi.presentation.auth.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.zlagi.domain.usecase.auth.signin.google.GoogleIdpAuthenticationInUseCase
import com.zlagi.domain.usecase.auth.status.AuthenticationStatusUseCase
import com.zlagi.presentation.TestCoroutineRule
import com.zlagi.presentation.viewmodel.auth.onboarding.OnBoardingContract
import com.zlagi.presentation.viewmodel.auth.onboarding.OnBoardingViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime


@ExperimentalTime
@ExperimentalCoroutinesApi
@MediumTest
class OnBoardingTestViewModel {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    private lateinit var googleIdpAuthenticationInUseCase: GoogleIdpAuthenticationInUseCase

    @MockK
    private lateinit var authenticationStatusUseCase: AuthenticationStatusUseCase

    // systemUnderTest
    private lateinit var sut: OnBoardingViewModel

    @Before
    fun setup() {
        // Turn relaxUnitFun on for all mocks
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = OnBoardingViewModel(
            googleIdpAuthenticationInUseCase = googleIdpAuthenticationInUseCase,
            authenticationStatusUseCase = authenticationStatusUseCase
        )
    }

    @Test
    fun `when EmailSignInButtonClicked then verify viewEffect`() = testCoroutineRule.runBlockingTest {
        // Given
        val expectedViewEffect = OnBoardingContract.OnBoardingViewEffect.NavigateToSignIn

        // When
        sut.setEvent(OnBoardingContract.OnBoardingEvent.EmailSignInButtonClicked)

        // Then
        sut.viewEffect.test {
            val actual = awaitItem()

            // Assertion
            Truth.assertThat(actual).isEqualTo(expectedViewEffect)
            expectNoEvents()
        }
    }

}