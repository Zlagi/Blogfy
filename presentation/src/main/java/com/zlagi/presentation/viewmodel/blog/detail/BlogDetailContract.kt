package com.zlagi.presentation.viewmodel.blog.detail

import com.zlagi.presentation.model.BlogPresentationModel

class BlogDetailContract {

    sealed class BlogDetailEvent {
        object Initialization : BlogDetailEvent()
        object CheckBlogAuthor : BlogDetailEvent()
        object UpdateBlogButtonClicked : BlogDetailEvent()
        object DeleteBlogButtonClicked : BlogDetailEvent()
        object ConfirmDialogButtonClicked : BlogDetailEvent()
        object RefreshData : BlogDetailEvent()
    }

    sealed class BlogDetailViewEffect {
        data class NavigateToUpdateBlog(val blogPk: Int?) : BlogDetailViewEffect()
        object ShowDeleteBlogDialog : BlogDetailViewEffect()
        data class NavigateUp(val refreshLoad: Boolean) : BlogDetailViewEffect()
        data class ShowSnackBarError(val message: Int) : BlogDetailViewEffect()
    }

    data class BlogDetailViewState(
        val loading: Boolean = false,
        val isAuthor: Boolean = false,
        val blog: BlogPresentationModel? = null
    )
}