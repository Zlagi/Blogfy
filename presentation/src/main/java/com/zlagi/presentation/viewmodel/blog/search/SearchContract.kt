package com.zlagi.presentation.viewmodel.blog.search

import com.zlagi.presentation.model.BlogPresentationModel
import com.zlagi.presentation.model.SearchSuggestionPresentationModel

class SearchContract {

    sealed class SearchEvent {
        object Initialization: SearchEvent()
        data class SearchSuggestionViewExpandedChanged(val state: Boolean): SearchEvent()
        data class SearchSuggestionViewCollapsedChanged(val state: Boolean): SearchEvent()
        data class SearchBlogResultViewChanged(val state: Boolean): SearchEvent()
        data class SearchTextFocusedChanged(val state: Boolean): SearchEvent()
        data class NewSearch(val query: String): SearchEvent()
        object NextPage: SearchEvent()
        data class DeleteSearchSuggestionButtonClicked(val searchSuggestionPk: Int): SearchEvent()
        object DeleteAllSearchSuggestionsButtonClicked: SearchEvent()
    }

    sealed class SearchViewEffect {
        data class ShowSnackBarError(val message: Int): SearchViewEffect()
    }

    data class SearchViewState(
        val query: String = "",
        val loading: Boolean = false,
        val noSearchResults: Boolean = false,
        val blogs: List<BlogPresentationModel> = emptyList(),
        val searchSuggestions: List<SearchSuggestionPresentationModel> = emptyList(),
        var searchSuggestionViewExpanded: Boolean = true,
        var searchSuggestionViewCollapsed: Boolean = false,
        var searchBlogResultView: Boolean = false,
        var searchTextFocused: Boolean = false
    )
}