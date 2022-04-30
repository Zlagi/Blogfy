package com.zlagi.presentation.viewmodel.blog.search.historyview

import androidx.annotation.DrawableRes
import com.zlagi.presentation.R
import com.zlagi.presentation.model.HistoryPresentationModel

class SearchHistoryContract {

    sealed class SearchHistoryEvent {
        object LoadHistory : SearchHistoryEvent()
        data class HistoryItemClicked(val query: String) : SearchHistoryEvent()
        data class DeleteHistoryItem(val query: String) : SearchHistoryEvent()
        object ClearHistory : SearchHistoryEvent()
        data class UpdateFocusState(val state: Boolean) : SearchHistoryEvent()
        data class UpdateQuery(val query: String) : SearchHistoryEvent()
        object NavigateTo : SearchHistoryEvent()
        object NavigateUp : SearchHistoryEvent()
    }

    sealed class SearchHistoryViewEffect {
        data class NavigateTo(val query: String) : SearchHistoryViewEffect()
        data class NavigateUp(val query: String) : SearchHistoryViewEffect()
    }

    data class SearchHistoryViewState(
        val expansion: Boolean = true,
        val focus: Boolean = false,
        val query: String = "",
        val data: List<HistoryPresentationModel> = emptyList(),
        val emptyHistory: Boolean = true,
        @DrawableRes val icon: Int = R.drawable.ic_search,
    )

}