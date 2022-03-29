package com.zlagi.presentation.viewmodel.account.detail

import com.zlagi.presentation.model.AccountPresentationModel

class AccountDetailContract {

    sealed class AccountDetailEvent {
        object Initialization : AccountDetailEvent()
        object UpdatePasswordButtonClicked : AccountDetailEvent()
        object SignOutButtonClicked: AccountDetailEvent()
        object ConfirmDialogButtonClicked: AccountDetailEvent()
    }

    sealed class AccountDetailViewEffect {
        data class ShowSnackBarError(val message: Int): AccountDetailViewEffect()
        object ShowDiscardChangesDialog: AccountDetailViewEffect()
        object NavigateToUpdatePassword: AccountDetailViewEffect()
        object NavigateToAuth: AccountDetailViewEffect()
    }

    data class AccountDetailViewState(
        val loading: Boolean = true,
        val isSigningOut: Boolean = false,
        val account: AccountPresentationModel? = null
    )
}