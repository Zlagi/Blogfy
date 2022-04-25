package com.zlagi.presentation.viewmodel.auth.signin

import androidx.annotation.StringRes
import com.zlagi.presentation.R

class SignInContract {

    sealed class SignInEvent {
        data class EmailChanged(val email: String) : SignInEvent()
        data class PasswordChanged(val password: String) : SignInEvent()
        object SignInButtonClicked : SignInEvent()
        object SignUpTextViewClicked : SignInEvent()
    }

    sealed class SignInViewEffect {
        data class ShowSnackBarError(val message: Int): SignInViewEffect()
        object NavigateToSignUp: SignInViewEffect()
        object NavigateToFeed: SignInViewEffect()
    }

    data class SignInViewState(
        val email: String = "",
        val password: String = "",
        val loading: Boolean = false,
        @StringRes val emailError: Int = R.string.no_error_message,
        @StringRes val passwordError: Int = R.string.no_error_message,
    )
}
