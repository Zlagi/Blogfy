package com.zlagi.presentation.viewmodel.blog.update

import android.net.Uri
import com.zlagi.presentation.model.BlogPresentationModel

class UpdateBlogContract {

    sealed class UpdateBlogEvent {

        object Initialization : UpdateBlogEvent()

        data class TitleChanged(
            val title: String
        ) : UpdateBlogEvent()

        data class DescriptionChanged(
            val description: String,
        ) : UpdateBlogEvent()

        data class OriginalUriChanged(
            val uri: Uri?
        ) : UpdateBlogEvent()

        data class ConfirmUpdateButtonClicked(val updateTime: String, val imageUri: Uri?) : UpdateBlogEvent()

        object CancelUpdateButtonClicked : UpdateBlogEvent()

        object ConfirmDialogButtonClicked : UpdateBlogEvent()
    }

    sealed class UpdateBlogViewEffect {
        data class ShowSnackBarError(val message: Int) : UpdateBlogViewEffect()
        object ShowDiscardChangesDialog : UpdateBlogViewEffect()
        object NavigateUp : UpdateBlogViewEffect()
        object ShowToast : UpdateBlogViewEffect()
    }

    data class UpdateBlogViewState(
        val loading: Boolean = false,
        var blog: BlogPresentationModel? = null,
        val originalUri: Uri? = null
    )
}