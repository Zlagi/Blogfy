package com.zlagi.presentation.viewmodel.blog.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.R
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.PaginationDomainModel
import com.zlagi.domain.usecase.blog.feed.GetFeedUseCase
import com.zlagi.domain.usecase.blog.feed.RequestMoreBlogsUseCase
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.*
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedEvent.*
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedViewEffect.Navigate
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedViewEffect.ShowSnackBarError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SHOULD_REFRESH = "should_refresh"
const val SHEET_DIALOG_ITEM = "sheet_dialog"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val requestMoreBlogsUseCase: RequestMoreBlogsUseCase,
    private val blogDomainPresentationMapper: BlogDomainPresentationMapper
) : ViewModel() {

    companion object {
        private const val page_size = PaginationDomainModel.DEFAULT_PAGE_SIZE
        private const val query = ""
    }

    private val currentState: FeedViewState
        get() = viewState.value

    private val _viewState = MutableStateFlow(FeedViewState())
    val viewState: StateFlow<FeedViewState> get() = _viewState

    private val _viewEffect: Channel<FeedViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job = Job()

    var isLoadingNextPage: Boolean = false
    var isLastPage = false

    private var currentPage = 0

    /**
     * Handle events
     */
    fun setEvent(event: FeedEvent) {
        when (event) {
            is Initialization -> onInitialization()
            is NextPage -> onNextPage(false)
            is SwipeRefresh -> onRefresh()
            is BlogItemClicked -> onNavigation(event.blogPk)
            is CreateBlogButtonClicked -> onNavigation(null)
        }
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> FeedViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: FeedViewState.() -> FeedViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Start initializing feed
     */
    private fun onInitialization() {
        if (currentState.results.isEmpty()) {
            resetPagination()
            onNextPage(true)
        }
    }

    /**
     * Start fetching cached blogs
     */
    private fun onBlogsObtained() {
        viewModelScope.launch {
            getFeedUseCase(query).let { blogList ->
                blogDomainPresentationMapper.fromList(blogList).let {
                    setState { copy(loading = false, results = it) }
                }
            }
        }
    }

    /**
     * Start requesting more blogs
     */
    private fun onNextPage(refreshLoad: Boolean) {
        setState { copy(loading = true) }
        isLoadingNextPage = true
        job.cancel()
        job = viewModelScope.launch {
            when (val result =
                requestMoreBlogsUseCase(refreshLoad, query, ++currentPage, page_size)) {
                is DataResult.Success -> {
                    isLoadingNextPage = false
                    onPaginationInfoObtained(result.data.pagination)
                    setState { copy(noResults = false) }
                    onBlogsObtained()
                }
                is DataResult.Error -> {
                    val error = result.exception.getStringResId()
                    onEmptyFeed(error)
                    onBlogsObtained()
                }
            }
        }
    }

    /**
     * Start refreshing blogs
     */
    private fun onRefresh() {
        resetPagination()
        onNextPage(true)
    }

    /**
     * Set new destination
     */
    private fun onNavigation(blogPk: Int?) {
        setEffect { Navigate(blogPk) }
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

    /**
     * Handle empty results
     */
    private fun onEmptyFeed(error: Int) {
        if (error == R.string.no_more_results) {
            setState { copy(loading = false, results = emptyList(), noResults = true) }
        } else {
            setEffect { ShowSnackBarError(error) }
        }
    }
}
