package com.zlagi.presentation.viewmodel.auth.onboarding

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.auth.signin.google.GoogleIdpAuthenticationInUseCase
import com.zlagi.domain.usecase.auth.status.AuthenticationStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val googleIdpAuthenticationInUseCase: GoogleIdpAuthenticationInUseCase,
    private val authenticationStatusUseCase: AuthenticationStatusUseCase
) : ViewModel() {

    private val currentState: OnBoardingContract.OnBoardingViewState
        get() = viewState.value

    val viewState: StateFlow<OnBoardingContract.OnBoardingViewState> get() = _viewState
    private val _viewState = MutableStateFlow(OnBoardingContract.OnBoardingViewState())

    private val _viewEffect: Channel<OnBoardingContract.OnBoardingViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job = Job()

    /**
     * Handle events
     */
    fun setEvent(event: OnBoardingContract.OnBoardingEvent) {
        when (event) {
            is OnBoardingContract.OnBoardingEvent.GoogleSignInButtonClicked -> setGoogleSignIn(event.data)
            is OnBoardingContract.OnBoardingEvent.EmailSignInButtonClicked -> setEffect { OnBoardingContract.OnBoardingViewEffect.NavigateToSignIn }
        }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: OnBoardingContract.OnBoardingViewState.() -> OnBoardingContract.OnBoardingViewState) {
        val newState = currentState.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> OnBoardingContract.OnBoardingViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Handle authentication status
     */
    fun checkAuthenticationStatus(): Boolean {
        return authenticationStatusUseCase()
    }

    /**
     * Handle google sign in event
     */
    private fun setGoogleSignIn(data: Intent) {
        job.cancel()
        job = viewModelScope.launch {
            setState { copy(loading = true) }
            when (val result = googleIdpAuthenticationInUseCase(data)) {
                is DataResult.Success -> setEffect { OnBoardingContract.OnBoardingViewEffect.NavigateToFeed }
                is DataResult.Error -> setEffect {
                    OnBoardingContract.OnBoardingViewEffect.ShowSnackBarError(
                        result.exception.getStringResId()
                    )
                }
            }
        }
    }
}