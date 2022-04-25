package com.zlagi.presentation.viewmodel.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.auth.signin.email.SignInUseCase
import com.zlagi.presentation.R.string.*
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.*
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInEvent.*
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewEffect.NavigateToFeed
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewEffect.ShowSnackBarError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val currentState: SignInViewState
        get() = viewState.value

    val viewState: StateFlow<SignInViewState> get() = _viewState
    private val _viewState = MutableStateFlow(SignInViewState())

    private val _viewEffect: Channel<SignInViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job = Job()

    /**
     * Handle events
     */
    fun setEvent(event: SignInEvent) {
        when (event) {
            is EmailChanged -> setState { copy(email = event.email) }
            is PasswordChanged -> setState { copy(password = event.password) }
            is SignUpTextViewClicked -> setEffect { SignInViewEffect.NavigateToSignUp }
            is SignInButtonClicked -> onSignIn()
        }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: SignInViewState.() -> SignInViewState) {
        val newState = currentState.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> SignInViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Handle email sign in event
     */
    private fun onSignIn() {
        val (email, password) = currentState
        job.cancel()
        job = viewModelScope.launch {
            setState { copy(loading = true) }
            val signInResult = signInUseCase(email, password)
            setState { copy(loading = false) }

            setEmailErrorState(signInResult.emailError)
            setPasswordErrorState(signInResult.passwordError)

            when (signInResult.result) {
                is DataResult.Success -> setEffect { NavigateToFeed }
                is DataResult.Error -> setEffect {
                    ShowSnackBarError((
                            signInResult.result as DataResult.Error<Unit>
                            ).exception.getStringResId()) }
                null -> return@launch
            }
        }
    }

    private fun setEmailErrorState(emailError: AuthError?) {
        emailError?.let {
            if (it == AuthError.EmptyField) setState { copy(emailError = empty_field_message) }
            else setState { copy(emailError = email_error_message) }
        } ?: setState { copy(emailError = no_error_message) }
    }

    private fun setPasswordErrorState(passwordError: AuthError?) {
        passwordError?.let {
            if (it == AuthError.EmptyField) setState { copy(passwordError = empty_field_message) }
            else setState { copy(passwordError = password_error_message) }
        } ?: setState { copy(passwordError = no_error_message) }

    }
}
