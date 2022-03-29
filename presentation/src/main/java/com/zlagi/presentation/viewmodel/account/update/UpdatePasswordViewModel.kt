package com.zlagi.presentation.viewmodel.account.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.AuthError
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.account.update.UpdatePasswordUseCase
import com.zlagi.presentation.R.string.*
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.*
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordEvent.*
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordViewEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val currentState: UpdatePasswordViewState
        get() = viewState.value

    val viewState: StateFlow<UpdatePasswordViewState> get() = _viewState
    private val _viewState = MutableStateFlow(UpdatePasswordViewState())

    private val _viewEffect: Channel<UpdatePasswordViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job? = null

    /**
     * Set handle events
     */
    fun setEvent(event: UpdatePasswordEvent) {
        when (event) {
            is CurrentPasswordChanged -> setState { copy(currentPassword = event.currentPassword) }
            is NewPasswordChanged -> setState { copy(newPassword = event.newPassword) }
            is ConfirmNewPassword -> setState { copy(confirmNewPassword = event.confirmNewPassword) }
            is ConfirmUpdatePasswordButtonClicked -> onUpdatePassword()
            is CancelUpdatePasswordButtonClicked -> setEffect { ShowDiscardChangesDialog }
            is ConfirmDialogButtonClicked -> setEffect { NavigateUp }
        }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: UpdatePasswordViewState.() -> UpdatePasswordViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> UpdatePasswordViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Start updating password
     */
    private fun onUpdatePassword() {
        job?.cancel()
        currentState.let {
            job = viewModelScope.launch {
                setState { copy(loading = true) }
                val updatePasswordResult = updatePasswordUseCase(
                    it.currentPassword,
                    it.newPassword,
                    it.confirmNewPassword
                )
                setState { copy(loading = false) }
                when {
                    updatePasswordResult.currentPasswordError != null -> setState {
                        copy(
                            currentPasswordError = password_error_message,
                            newPasswordError = no_error_message,
                            confirmNewPasswordError = no_error_message
                        )
                    }
                    updatePasswordResult.newPasswordError != null -> setState {
                        copy(
                            currentPasswordError = no_error_message,
                            newPasswordError = password_error_message,
                            confirmNewPasswordError = no_error_message
                        )
                    }
                    updatePasswordResult.confirmPasswordError != null ->
                        if (updatePasswordResult.confirmPasswordError == AuthError.InputTooShort)
                            setState {
                                copy(
                                    currentPasswordError = no_error_message,
                                    newPasswordError = no_error_message,
                                    confirmNewPasswordError = password_error_message
                                )
                            }
                        else setEffect { ShowSnackBarError(password_unmatched) }
                    else -> {
                        when (updatePasswordResult.result) {
                            is DataResult.Success -> {
                                setEffect { ShowToast }
                                setEffect { NavigateUp }
                            }
                            is DataResult.Error -> {
                                val exception = (updatePasswordResult.result as DataResult.Error<Unit>).exception
                                if (exception == NetworkException.BadRequest) {
                                    setEffect {
                                        ShowSnackBarError(
                                            invalid_credentials_message
                                        )
                                    }
                                }
                                else setEffect {
                                    ShowSnackBarError(
                                        exception.getStringResId()
                                    )
                                }
                            }
                            null -> return@launch
                        }
                    }
                }
            }
        }
    }
}