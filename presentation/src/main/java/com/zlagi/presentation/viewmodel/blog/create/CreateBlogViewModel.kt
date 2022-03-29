package com.zlagi.presentation.viewmodel.blog.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.create.CreateBlogUseCase
import com.zlagi.presentation.R.string.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract.CreateBlogEvent.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract.CreateBlogViewEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBlogViewModel @Inject constructor(
    private val createBlogUseCase: CreateBlogUseCase,
    private val storageReference: FirebaseStorage
) : ViewModel() {

    private val currentState: CreateBlogViewState
        get() = viewState.value

    val viewState: StateFlow<CreateBlogViewState> get() = _viewState
    private val _viewState = MutableStateFlow(CreateBlogViewState())

    private val _viewEffect: Channel<CreateBlogViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job? = null

    /**
     * handle events
     */
    fun setEvent(event: CreateBlogEvent) {
        when (event) {
            is TitleChanged -> setState { copy(title = event.title) }
            is DescriptionChanged -> setState { copy(description = event.description) }
            is OriginalUriChanged -> setState {
                copy(
                    originalUri = event.uri
                )
            }
            is CancelCreateButtonClicked -> setEffect { ShowDiscardChangesDialog }
            is ConfirmCreateButtonClicked -> onCreateBlog(event.creationTime, event.imageUri)
            is ConfirmDialogButtonClicked -> setEffect { NavigateUp }
        }
    }

    /**
     * Set new ui State
     */
    fun setState(reduce: CreateBlogViewState.() -> CreateBlogViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> CreateBlogViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Start creating blog
     */
    private fun onCreateBlog(creationTime: String, imageUri: Uri?) {
        val (title, description, blogImage) = currentState
        job?.cancel()
        job = viewModelScope.launch {
            setState { copy(loading = true) }
            val createBlogResult =
                createBlogUseCase(title, description, blogImage, creationTime)

            when {
                createBlogResult.titleError != null -> setEffect {
                    ShowSnackBarError(
                        title_error_message
                    )
                }

                createBlogResult.descriptionError != null -> setEffect {
                    ShowSnackBarError(
                        description_error_message
                    )
                }

                createBlogResult.uriError != null -> setEffect {
                    ShowSnackBarError(
                        uri_error_message
                    )
                }

                else -> {
                    when (createBlogResult.result) {
                        is DataResult.Success -> {
                            storageReference.getReference("image/$creationTime").putFile(imageUri!!)
                                .addOnSuccessListener {
                                    setEffect { ShowToast }
                                    setEffect { NavigateUp }
                                }
                        }
                        is DataResult.Error -> setEffect { ShowSnackBarError((createBlogResult.result as DataResult.Error<Unit>).exception.getStringResId()) }
                        null -> return@launch
                    }
                }
            }
        }
    }
}
