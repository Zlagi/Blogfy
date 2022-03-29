package com.zlagi.presentation.viewmodel.blog.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.PaginationDomainModel
import com.zlagi.domain.usecase.blog.search.GetBlogsByUseCase
import com.zlagi.domain.usecase.blog.search.RequestMoreBlogsUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.DeleteAllSearchSuggestionsUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.DeleteSearchSuggestionUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.GetSearchSuggestionsUseCase
import com.zlagi.domain.usecase.blog.search.suggestions.StoreSearchSuggestionUseCase
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.mapper.SearchSuggestionDomainPresentationMapper
import com.zlagi.presentation.model.SearchSuggestionPresentationModel
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.*
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchEvent.*
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchViewEffect.ShowSnackBarError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBlogViewModel @Inject constructor(
    private val getBlogsByUseCase: GetBlogsByUseCase,
    private val requestMoreBlogsUseCase: RequestMoreBlogsUseCase,
    private val storeSearchSuggestionUseCase: StoreSearchSuggestionUseCase,
    private val deleteSearchSuggestionUseCase: DeleteSearchSuggestionUseCase,
    private val deleteAllSearchSuggestionsUseCase: DeleteAllSearchSuggestionsUseCase,
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
    private val blogDomainPresentationMapper: BlogDomainPresentationMapper,
    private val searchDomainPresentationMapper: SearchSuggestionDomainPresentationMapper
) : ViewModel() {

    companion object {
        private const val page_size = PaginationDomainModel.DEFAULT_PAGE_SIZE
    }

    val currentState: SearchViewState
        get() = viewState.value

    private val _event: MutableSharedFlow<SearchEvent> = MutableSharedFlow()
    private val event = _event.asSharedFlow()

    private val _viewEffect: Channel<SearchViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private val _viewState = MutableStateFlow(SearchViewState())
    val viewState: StateFlow<SearchViewState> get() = _viewState

    var isLoadingNextPage: Boolean = false
    var isLastPage: Boolean = false
    private var currentPage = 0

    private var job: Job = Job()

    init {
        onSubscribe()
    }

    /**
     * Start listening to events
     */
    private fun onSubscribe() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    /**
     * Start fetching search suggestions
     */
    private fun onSuggestionsObtained() {
        viewModelScope.launch {
            getSearchSuggestionsUseCase().collect { suggestions ->
                searchDomainPresentationMapper.fromList(suggestions).takeLast(5).reversed().let {
                    setState { copy(searchSuggestions = it) }
                }
            }
        }
    }

    /**
     * Set new event
     */
    fun setEvent(event: SearchEvent) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    /**
     * Handle events
     */
    private fun handleEvent(event: SearchEvent) {
        when (event) {
            is Initialization -> onSuggestionsObtained()
            is SearchSuggestionViewExpandedChanged -> onExpandSearchSuggestionsView(event.state)
            is SearchSuggestionViewCollapsedChanged -> onCollapseSearchSuggestionsView(event.state)
            is SearchBlogResultViewChanged -> onSearchBlogResultView(event.state)
            is SearchTextFocusedChanged -> onSearchTextFocusedChanged(event.state)
            is BlogItemClicked -> onNavigation(event.blogPk)
            is NextPage -> onNextPage(false)
            is NewSearch -> onSearch(event.query)
            is DeleteSearchSuggestionButtonClicked -> onDeleteSearchSuggestion(event.searchSuggestionPk)
            is DeleteAllSearchSuggestionsButtonClicked -> onDeleteAllSearchSuggestions()
        }
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> SearchViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Set new ui state
     */
    private fun setState(reduce: SearchViewState.() -> SearchViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }


    /**
     * Set expanded search suggestions view
     */
    private fun onExpandSearchSuggestionsView(state: Boolean) {
        setState {
            copy(
                searchSuggestionViewExpanded = state
            )
        }
    }


    /**
     * Set collapsed search suggestions view
     */
    private fun onCollapseSearchSuggestionsView(state: Boolean) {
        if (state) {
            setState { copy(loading = false) }
            job.cancel()
        }
        setState {
            copy(
                searchSuggestionViewCollapsed = state
            )
        }
    }

    /**
     * Set search blog result view
     */
    private fun onSearchBlogResultView(state: Boolean) {
        setState {
            copy(
                searchBlogResultView = state
            )
        }
    }

    /**
     * Set search text focus
     */
    private fun onSearchTextFocusedChanged(state: Boolean) {
        setState {
            copy(
                searchTextFocused = state
            )
        }
    }

    /**
     * Set new destination
     */
    private fun onNavigation(blogPk: Int) {
        setEffect { SearchViewEffect.Navigate(blogPk) }
    }

    /**
     * Start searching blogs
     */
    private fun onSearch(query: String) {
        setState { copy(query = query, searchSuggestionViewCollapsed = false, searchSuggestionViewExpanded = false, searchBlogResultView = true, searchTextFocused = true) }
        resetPagination()
        onNextPage(true)
    }

    /**
     * Validate search query
     */
    private suspend fun validateSearchQuery(searchQuery: String) {
        val queryExist = currentState.searchSuggestions.find {
            it.query == searchQuery
        }
        if (queryExist == null) {
            storeSearchSuggestionUseCase(searchQuery)
        }
    }

    /**
     * Delete search suggestion item
     */
    private fun onDeleteSearchSuggestion(searchSuggestionPk: Int) {
        viewModelScope.launch {
            val oldBlog = currentState.searchSuggestions.find { it.pk == searchSuggestionPk }
            val suggestions: ArrayList<SearchSuggestionPresentationModel> = ArrayList()
            currentState.searchSuggestions.forEach {
                suggestions.add(it)
            }
            suggestions.remove(oldBlog)
            setState { copy(searchSuggestions = suggestions) }
            deleteSearchSuggestionUseCase(searchSuggestionPk)
        }
    }

    /**
     * Delete all search suggestions items
     */
    private fun onDeleteAllSearchSuggestions() {
        viewModelScope.launch {
            setState { copy(searchSuggestions = emptyList()) }
            deleteAllSearchSuggestionsUseCase()
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
     * Start requesting more blogs
     */
    private fun onNextPage(refreshLoad: Boolean) {
        setState { copy(loading = true) }
        isLoadingNextPage = true
        job.cancel()
        job = viewModelScope.launch {
            validateSearchQuery(currentState.query)
            when (val result =
                requestMoreBlogsUseCase(
                    refreshLoad,
                    currentState.query,
                    ++currentPage,
                    page_size
                )) {
                is DataResult.Success -> {
                    setState { copy(noSearchResults = false) }
                    isLoadingNextPage = false
                    onPaginationInfoObtained(result.data.pagination)
                    onBlogsObtained()
                }
                is DataResult.Error -> {
                    setState { copy(blogs = emptyList(), noSearchResults = true) }
                    val error = result.exception.getStringResId()
                    setEffect { ShowSnackBarError(error) }
                }
            }
        }
    }

    /**
     * Start loading cached blogs
     */
    private fun onBlogsObtained() {
        viewModelScope.launch {
            getBlogsByUseCase("").map {
                blogDomainPresentationMapper.from(it)
            }.let {
                setState { copy(blogs = it, loading = false) }
            }
        }
    }

    /**
     * Handle Pagination Info
     */
    private fun onPaginationInfoObtained(pagination: PaginationDomainModel) {
        currentPage = pagination.currentPage
        isLastPage = !pagination.canLoadMore
    }
}
