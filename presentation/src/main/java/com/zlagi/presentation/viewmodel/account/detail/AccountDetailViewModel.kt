package com.zlagi.presentation.viewmodel.account.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.taskmanager.TaskManager
import com.zlagi.domain.taskmanager.TaskState
import com.zlagi.domain.usecase.account.delete.DeleteAccountUseCase
import com.zlagi.domain.usecase.account.detail.GetAccountUseCase
import com.zlagi.domain.usecase.account.detail.SyncAccountUseCase
import com.zlagi.domain.usecase.auth.deletetokens.DeleteTokensUseCase
import com.zlagi.domain.usecase.blog.search.history.ClearHistoryUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.mapper.AccountDomainPresentationMapper
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailEvent.*
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailViewEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val syncAccountUseCase: SyncAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val deleteTokensUseCase: DeleteTokensUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val taskManager: TaskManager,
    private val accountDomainPresentationMapper: AccountDomainPresentationMapper
) : ViewModel() {

    private val currentState: AccountDetailContract.AccountDetailViewState
        get() = viewState.value

    val viewState: StateFlow<AccountDetailContract.AccountDetailViewState> get() = _viewState
    private val _viewState = MutableStateFlow(AccountDetailContract.AccountDetailViewState())

    private val _viewEffect: Channel<AccountDetailContract.AccountDetailViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job? = null

    /**
     * Set new Ui State
     */
    private fun setState(
        reduce: AccountDetailContract.AccountDetailViewState.() -> AccountDetailContract.AccountDetailViewState
    ) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> AccountDetailContract.AccountDetailViewEffect) {
        val effectValue = builder()
        setState { copy(isSigningOut = false, loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Set handle events
     */
    fun setEvent(event: AccountDetailContract.AccountDetailEvent) {
        when (event) {
            is Initialization -> onRequestAccount()
            is UpdatePasswordButtonClicked -> setEffect { NavigateToUpdatePassword }
            is SignOutButtonClicked -> setEffect { ShowDiscardChangesDialog }
            is ConfirmDialogButtonClicked -> onSignOut()
        }
    }

    /**
     * Start syncing account properties
     */
    private fun onRequestAccount() {
        if (currentState.account == null) {
            job?.cancel()
            job = viewModelScope.launch {
                when (val result = syncAccountUseCase()) {
                    is DataResult.Success -> {
                        onGetAccount()
                    }
                    is DataResult.Error -> {
                        setEffect { ShowSnackBarError(result.exception.getStringResId()) }
                    }
                }
                onPeriodicSyncAccount()
            }
        }
    }

    private suspend fun onPeriodicSyncAccount() {
        val task = taskManager.syncAccount()
        taskManager.observeTask(task).collect {
            when (it) {
                TaskState.SCHEDULED, TaskState.COMPLETED, TaskState.CANCELLED -> {
                    setState { copy(loading = false) }
                    onGetAccount()
                }
                TaskState.FAILED -> setEffect { ShowSnackBarError(R.string.sync_error) }
            }
        }
    }

    /**
     * Start fetching cached account
     */
    private fun onGetAccount() {
        viewModelScope.launch {
            getAccountUseCase().let {
                val account = accountDomainPresentationMapper.from(it)
                setState { copy(account = account, loading = false) }
            }
        }
    }

    /**
     * Start signing out
     */
    private fun onSignOut() {
        setState { copy(isSigningOut = true) }
        job?.cancel()
        job = viewModelScope.launch {
            taskManager.abortAllTasks()
            deleteAccountUseCase()
            clearHistoryUseCase()
            deleteTokensUseCase()
            setEffect { NavigateToAuth }
        }
    }
}
