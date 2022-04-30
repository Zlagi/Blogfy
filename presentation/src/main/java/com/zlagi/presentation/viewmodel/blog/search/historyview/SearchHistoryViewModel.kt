package com.zlagi.presentation.viewmodel.blog.search.historyview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.search.history.ClearHistoryUseCase
import com.zlagi.domain.usecase.blog.search.history.DeleteQueryUseCase
import com.zlagi.domain.usecase.blog.search.history.GetHistoryUseCase
import com.zlagi.domain.usecase.blog.search.history.SaveQueryUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.mapper.HistoryDomainPresentationMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val QUERY = "query"

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val saveQueryUseCase: SaveQueryUseCase,
    private val deleteQueryUseCase: DeleteQueryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val historyDomainPresentationMapper: HistoryDomainPresentationMapper
) : ViewModel() {

    val currentState: SearchHistoryContract.SearchHistoryViewState
        get() = viewState.value

    private val _viewEffect: Channel<SearchHistoryContract.SearchHistoryViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private val _viewState = MutableStateFlow(SearchHistoryContract.SearchHistoryViewState())
    val viewState: StateFlow<SearchHistoryContract.SearchHistoryViewState> get() = _viewState

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> SearchHistoryContract.SearchHistoryViewEffect) {
        val effectValue = builder()
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Set new ui state
     */
    private fun setState(
        reduce: SearchHistoryContract.SearchHistoryViewState.()
        -> SearchHistoryContract.SearchHistoryViewState
    ) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Handle events
     */
    fun setEvent(event: SearchHistoryContract.SearchHistoryEvent) {
        when (event) {
            SearchHistoryContract.SearchHistoryEvent.LoadHistory -> {
                onHistoryObtained()
            }
            is SearchHistoryContract.SearchHistoryEvent.HistoryItemClicked -> {
                setState {
                    copy(
                        query = event.query
                    )
                }
            }
            is SearchHistoryContract.SearchHistoryEvent.DeleteHistoryItem -> {
                onDeleteQuery(
                    query = event.query
                )
            }
            SearchHistoryContract.SearchHistoryEvent.ClearHistory -> {
                onClearHistory()
            }
            is SearchHistoryContract.SearchHistoryEvent.UpdateFocusState -> {
                setState {
                    copy(
                        expansion = !event.state,
                        focus = event.state,
                        icon = setIconState(event.state)
                    )
                }
            }
            is SearchHistoryContract.SearchHistoryEvent.UpdateQuery -> {
                setState { copy(query = event.query) }
            }
            is SearchHistoryContract.SearchHistoryEvent.NavigateTo -> {
                onSaveQuery(query = currentState.query)
                setEffect {
                    SearchHistoryContract.SearchHistoryViewEffect.NavigateTo(
                        query = currentState.query
                    )
                }
            }
            SearchHistoryContract.SearchHistoryEvent.NavigateUp -> {
                setEffect {
                    SearchHistoryContract.SearchHistoryViewEffect.NavigateUp(query = currentState.query)
                }
            }
        }
    }

    /**
     * Start loading cached search history
     */
    private fun onHistoryObtained() {
        viewModelScope.launch {
            when (val result = getHistoryUseCase()) {
                is DataResult.Success -> {
                    historyDomainPresentationMapper.fromList(result.data).let {
                        setState { copy(data = it, emptyHistory = false) }
                    }
                }
                is DataResult.Error -> {
                    setState {
                        copy(
                            data = emptyList(),
                            emptyHistory = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Delete search query
     */
    private fun onDeleteQuery(query: String) {
        viewModelScope.launch {
            deleteQueryUseCase(query = query)
            onHistoryObtained()
        }
    }

    /**
     * Clear search history
     */
    private fun onClearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
            onHistoryObtained()
        }
    }


    /**
     * Set icon state
     */
    private fun setIconState(state: Boolean): Int {
        return when {
            state -> R.drawable.ic_arrow_left
            else -> R.drawable.ic_search
        }
    }

    /**
     * Save search query
     */
    private fun onSaveQuery(query: String) {
        viewModelScope.launch {
            saveQueryUseCase(query)
            onHistoryObtained()
        }
    }
}
