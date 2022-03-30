package com.zlagi.presentation.viewmodel.blog.create

import android.net.Uri

class CreateBlogContract {

    sealed class CreateBlogEvent {
        data class TitleChanged(
            val title: String,
        ) : CreateBlogEvent()

        data class DescriptionChanged(
            val description: String,
        ) : CreateBlogEvent()

        data class OriginalUriChanged(
            val uri: Uri?
        ) : CreateBlogEvent()

        data class ConfirmCreateButtonClicked(val imageUri: Uri?) : CreateBlogEvent()

        object CancelCreateButtonClicked : CreateBlogEvent()

        object ConfirmDialogButtonClicked: CreateBlogEvent()
    }

    sealed class CreateBlogViewEffect {
        data class ShowSnackBarError(val message: Int): CreateBlogViewEffect()
        object ShowToast: CreateBlogViewEffect()
        object ShowDiscardChangesDialog: CreateBlogViewEffect()
        object NavigateUp: CreateBlogViewEffect()
    }

    data class CreateBlogViewState(
        val title: String = "",
        val description: String = "",
        val originalUri: Uri? = null,
        val loading: Boolean = false,
    )
}