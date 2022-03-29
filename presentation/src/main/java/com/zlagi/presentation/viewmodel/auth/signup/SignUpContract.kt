package com.zlagi.presentation.viewmodel.auth.signup

import androidx.annotation.StringRes
import com.zlagi.presentation.R

class SignUpContract {

    sealed class SignUpEvent {
        data class EmailChanged(val email: String) : SignUpEvent()
        data class UsernameChanged(val username: String) : SignUpEvent()
        data class PasswordChanged(val password: String) : SignUpEvent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent()
        object SignUpButtonClicked : SignUpEvent()
    }

    sealed class SignUpViewEffect {
        data class ShowSnackBarError(val message: Int): SignUpViewEffect()
        object NavigateToFeed: SignUpViewEffect()
    }

    data class SignUpViewState(
        val email: String = "",
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val loading: Boolean = false,
        @StringRes val emailError: Int = R.string.no_error_message,
        @StringRes val usernameError: Int = R.string.no_error_message,
        @StringRes val passwordError: Int = R.string.no_error_message,
        @StringRes val confirmPasswordError: Int = R.string.no_error_message,
    )
}