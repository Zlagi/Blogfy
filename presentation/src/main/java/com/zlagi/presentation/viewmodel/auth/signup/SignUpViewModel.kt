package com.zlagi.presentation.viewmodel.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.auth.signup.SignUpUseCase
import com.zlagi.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val currentState: SignUpContract.SignUpViewState
        get() = viewState.value

    val viewState: StateFlow<SignUpContract.SignUpViewState> get() = _viewState
    private val _viewState = MutableStateFlow(SignUpContract.SignUpViewState())

    private val _viewEffect: Channel<SignUpContract.SignUpViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job = Job()

    /**
     * Handle events
     */
    fun setEvent(event: SignUpContract.SignUpEvent) {
        when (event) {
            is SignUpContract.SignUpEvent.EmailChanged -> setState { copy(email = event.email) }
            is SignUpContract.SignUpEvent.UsernameChanged -> setState { copy(username = event.username) }
            is SignUpContract.SignUpEvent.PasswordChanged -> setState { copy(password = event.password) }
            is SignUpContract.SignUpEvent.ConfirmPasswordChanged -> setState { copy(confirmPassword = event.confirmPassword) }
            is SignUpContract.SignUpEvent.SignUpButtonClicked -> onSignUp()
        }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: SignUpContract.SignUpViewState.() -> SignUpContract.SignUpViewState) {
        val newState = currentState.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> SignUpContract.SignUpViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Handle email sign up event
     */
    private fun onSignUp() {
        job.cancel()
        currentState.let {
            job = viewModelScope.launch {
                setState { copy(loading = true) }
                val signUpResult =
                    signUpUseCase(it.email, it.username, it.password, it.confirmPassword)
                setState { copy(loading = false) }

                signUpResult.emailError?.let {
                    if (it == AuthError.EmptyField) setState { copy(emailError = R.string.empty_field_message) }
                    else setState { copy(emailError = R.string.email_error_message) }
                } ?: setState { copy(emailError = R.string.no_error_message) }

                signUpResult.usernameError?.let {when (it) {
                    is AuthError.EmptyField -> setState { copy(usernameError = R.string.empty_field_message) }
                    is AuthError.InputTooShort -> setState { copy(usernameError = R.string.username_error_message2) }
                    else -> setState { copy(usernameError = R.string.username_error_message) }
                }
                } ?: setState { copy(usernameError = R.string.no_error_message) }

                signUpResult.passwordError?.let {
                    if (it == AuthError.EmptyField) setState { copy(passwordError = R.string.empty_field_message) }
                    else setState { copy(passwordError = R.string.password_error_message) }
                } ?: setState { copy(passwordError = R.string.no_error_message) }

                signUpResult.confirmPasswordError?.let {
                    when (it) {
                        is AuthError.EmptyField -> setState { copy(confirmPasswordError = R.string.empty_field_message) }
                        is AuthError.InputTooShort -> setState { copy(confirmPasswordError = R.string.password_error_message) }
                        else -> setState { copy(confirmPasswordError = R.string.password_unmatched) }
                    }
                } ?: setState { copy(confirmPasswordError = R.string.no_error_message) }

                when (signUpResult.result) {
                    is DataResult.Success -> setEffect { SignUpContract.SignUpViewEffect.NavigateToFeed }
                    is DataResult.Error -> setEffect {
                        SignUpContract.SignUpViewEffect.ShowSnackBarError(
                            (signUpResult.result as DataResult.Error<Unit>).exception.getStringResId()
                        )
                    }
                    null -> return@launch
                }
            }
        }
    }
}