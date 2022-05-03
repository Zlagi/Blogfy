package com.zlagi.presentation.viewmodel.blog.search.resultview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.PaginationDomainModel
import com.zlagi.domain.usecase.blog.search.GetSearchUseCase
import com.zlagi.domain.usecase.blog.search.RequestMoreBlogsUseCase
import com.zlagi.domain.usecase.blog.search.history.SaveQueryUseCase
import com.zlagi.presentation.R
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val requestMoreBlogsUseCase: RequestMoreBlogsUseCase,
    private val getSearchUseCase: GetSearchUseCase,
    private val saveQueryUseCase: SaveQueryUseCase,
    private val blogDomainPresentationMapper: BlogDomainPresentationMapper
) : ViewModel() {

    companion object {
        private const val page_size = PaginationDomainModel.DEFAULT_PAGE_SIZE
    }

    val currentState: SearchResultContract.SearchResultViewState
        get() = viewState.value

    private val _viewEffect: Channel<SearchResultContract.SearchResultViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private val _viewState = MutableStateFlow(SearchResultContract.SearchResultViewState())
    val viewState: StateFlow<SearchResultContract.SearchResultViewState> get() = _viewState

    var isLoadingNextPage: Boolean = false
    var isLastPage: Boolean = false
    private var currentPage = 0

    private var job: Job = Job()

    /**
     * Handle events
     */
    fun setEvent(event: SearchResultContract.SearchResultEvent) {
        when (event) {
            is SearchResultContract.SearchResultEvent.UpdateIcon -> {
                setState {
                    copy(
                        icon = getIcon(state = true)
                    )
                }
            }
            is SearchResultContract.SearchResultEvent.UpdateQuery -> {
                setState { copy(query = event.query) }
            }
            is SearchResultContract.SearchResultEvent.ExecuteSearch -> {
                if (event.query.isNotEmpty()) {
                    resetPagination()
                    addQueryToHistory(alreadySaved = event.initSearch, query = event.query)
                    onSearch(refreshLoad = true, query = event.query)
                }
            }
            SearchResultContract.SearchResultEvent.NextPage -> {
                onSearch(refreshLoad = false, query = currentState.query)
            }
            is SearchResultContract.SearchResultEvent.NavigateUp -> {
                event.icon?.let {
                    if (it == R.drawable.ic_arrow_left) {
                        setEffect { SearchResultContract.SearchResultViewEffect.NavigateUp(query = currentState.query) }
                    }
                } ?: setEffect { SearchResultContract.SearchResultViewEffect.NavigateUp(query = currentState.query) }
            }
        }
    }

    /**
     * Set new ui state
     */
    private fun setState(
        reduce: SearchResultContract.SearchResultViewState.()
        -> SearchResultContract.SearchResultViewState
    ) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> SearchResultContract.SearchResultViewEffect) {
        val effectValue = builder()
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Save search query
     */
    private fun addQueryToHistory(alreadySaved: Boolean, query: String) {
        viewModelScope.launch {
            if (!alreadySaved) saveQueryUseCase(query = query)
        }
    }

    /**
     * Start requesting more blogs
     */
    private fun onSearch(refreshLoad: Boolean, query: String) {
        setState { copy(query = query, icon = getIcon(state = false)) }
        viewModelScope.launch {
            isLoadingNextPage = true
            setState { copy(loading = true) }
            job.cancel()
            job = viewModelScope.launch {
                when (val result =
                    requestMoreBlogsUseCase(
                        refreshLoad = refreshLoad,
                        searchQuery = query,
                        page = ++currentPage,
                        pageSize = page_size
                    )) {
                    is DataResult.Success -> {
                        isLoadingNextPage = false
                        setState { copy(showEmptyBlogs = false, loading = false) }
                        onPaginationInfoObtained(pagination = result.data.pagination)
                        onBlogsObtained()
                    }
                    is DataResult.Error -> {
                        setState {
                            copy(
                                blogs = emptyList(),
                                showEmptyBlogs = true,
                                loading = false
                            )
                        }
                        val error = result.exception.getStringResId()
                        setEffect {
                            SearchResultContract.SearchResultViewEffect.ShowSnackBarError(
                                error
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Reset pagination parameters
     */
    private fun resetPagination() {
        currentPage = 0
        isLastPage = false
        isLoadingNextPage = false
    }

    /**
     * Handle pagination info
     */
    private fun onPaginationInfoObtained(pagination: PaginationDomainModel) {
        currentPage = pagination.currentPage
        isLastPage = !pagination.canLoadMore
    }

    private fun getIcon(state: Boolean): Int {
        return when {
            state -> R.drawable.ic_arrow_left
            else -> R.drawable.ic_search
        }
    }

    /**
     * Start loading cached blogs
     */
    private fun onBlogsObtained() {
        viewModelScope.launch {
            val data = getSearchUseCase("")
            blogDomainPresentationMapper.fromList(data).let {
                setState { copy(blogs = it) }
            }
        }
    }
}
