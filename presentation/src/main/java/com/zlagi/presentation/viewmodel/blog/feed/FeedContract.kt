package com.zlagi.presentation.viewmodel.blog.feed

import com.zlagi.presentation.model.BlogPresentationModel

class FeedContract {

    sealed class FeedEvent {
        object Initialization : FeedEvent()
        object NextPage : FeedEvent()
        object SwipeRefresh : FeedEvent()
        object CreateBlogButtonClicked : FeedEvent()
        data class BlogItemClicked(val blogPk: Int?) : FeedEvent()
    }

    sealed class FeedViewEffect {
        data class Navigate(val blogPk: Int?) : FeedViewEffect()
        data class ShowSnackBarError(val error: Int) : FeedViewEffect()
    }

    data class FeedViewState(
        val loading: Boolean = false,
        val noResults: Boolean = false,
        val results: List<BlogPresentationModel> = emptyList()
    )
}
