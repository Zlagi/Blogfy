package com.zlagi.presentation.viewmodel.account.update

import androidx.annotation.StringRes
import com.zlagi.presentation.R

class UpdatePasswordContract {

    sealed class UpdatePasswordEvent {

        data class CurrentPasswordChanged(
            val currentPassword: String
        ) : UpdatePasswordEvent()

        data class NewPasswordChanged(
            val newPassword: String
        ) : UpdatePasswordEvent()

        data class ConfirmNewPassword(
            val confirmNewPassword: String
        ) : UpdatePasswordEvent()

        object ConfirmUpdatePasswordButtonClicked : UpdatePasswordEvent()

        object CancelUpdatePasswordButtonClicked : UpdatePasswordEvent()

        object ConfirmDialogButtonClicked : UpdatePasswordEvent()
    }

    sealed class UpdatePasswordViewEffect {
        data class ShowSnackBarError(val message: Int) : UpdatePasswordViewEffect()
        object ShowDiscardChangesDialog: UpdatePasswordViewEffect()
        object NavigateUp : UpdatePasswordViewEffect()
        object ShowToast : UpdatePasswordViewEffect()
    }

    data class UpdatePasswordViewState(
        val loading: Boolean = false,
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmNewPassword: String = "",
        @StringRes val currentPasswordError: Int = R.string.no_error_message,
        @StringRes val newPasswordError: Int = R.string.no_error_message,
        @StringRes val confirmNewPasswordError: Int = R.string.no_error_message
    )

}
