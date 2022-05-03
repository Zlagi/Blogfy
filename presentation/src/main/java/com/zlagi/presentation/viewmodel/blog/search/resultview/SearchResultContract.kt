package com.zlagi.presentation.viewmodel.blog.search.resultview

import androidx.annotation.DrawableRes
import com.zlagi.presentation.R
import com.zlagi.presentation.model.BlogPresentationModel


class SearchResultContract {

    sealed class SearchResultEvent {
        object UpdateIcon : SearchResultEvent()
        data class UpdateQuery(val query: String) : SearchResultEvent()
        data class ExecuteSearch(val initSearch: Boolean, val query: String) : SearchResultEvent()
        object NextPage : SearchResultEvent()
        data class NavigateUp(val icon: Int?) : SearchResultEvent()
    }

    sealed class SearchResultViewEffect {
        data class ShowSnackBarError(val message: Int) : SearchResultViewEffect()
        object ClearFocus: SearchResultViewEffect()
        object HideKeyboard: SearchResultViewEffect()
        data class NavigateUp(val query: String) : SearchResultViewEffect()
    }

    data class SearchResultViewState(
        val loading: Boolean = false,
        val query: String = "",
        @DrawableRes val icon: Int = R.drawable.ic_search,
        val blogs: List<BlogPresentationModel> = emptyList(),
        val showEmptyBlogs: Boolean = false
    )

}
