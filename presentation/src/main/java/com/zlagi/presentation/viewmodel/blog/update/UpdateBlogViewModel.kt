package com.zlagi.presentation.viewmodel.blog.update

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.dateformat.DateFormatUseCase
import com.zlagi.domain.usecase.blog.detail.GetBlogUseCase
import com.zlagi.domain.usecase.blog.update.UpdateBlogUseCase
import com.zlagi.presentation.R.string.description_error_message
import com.zlagi.presentation.R.string.title_error_message
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.*
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogEvent.*
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogViewEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBlogViewModel @Inject constructor(
    private val getBlogUseCase: GetBlogUseCase,
    private val updateBlogUseCase: UpdateBlogUseCase,
    private val dateFormatUseCase: DateFormatUseCase,
    private val blogDomainPresentationMapper: BlogDomainPresentationMapper,
    private val storageReference: FirebaseStorage
) : ViewModel() {

    private val currentState: UpdateBlogViewState
        get() = viewState.value

    val viewState: StateFlow<UpdateBlogViewState> get() = _viewState
    private val _viewState = MutableStateFlow(UpdateBlogViewState())

    private val _viewEffect: Channel<UpdateBlogViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job? = null

    /**
     * handle events
     */
    fun setEvent(event: UpdateBlogEvent) {
        when (event) {
            is Initialization -> onBlogObtained(blogPk = event.pk)
            is TitleChanged -> onUpdateTitle(title = event.title)
            is DescriptionChanged -> onUpdateDescription(description = event.description)
            is OriginalUriChanged -> onUpdateOriginalUri(uri = event.uri)
            is ConfirmUpdateButtonClicked -> onUpdateBlog(
                blogPk = currentState.blog?.pk,
                imageUri = event.imageUri
            )
            is CancelUpdateButtonClicked -> setEffect { ShowDiscardChangesDialog }
            is ConfirmDialogButtonClicked -> setEffect { NavigateUp }
        }
    }

    /**
     * Set new Ui State
     */
    private fun setState(reduce: UpdateBlogViewState.() -> UpdateBlogViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new Effect
     */
    private fun setEffect(builder: () -> UpdateBlogViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Set blog title
     */
    private fun onUpdateTitle(title: String) {
        currentState.blog?.let { blogPost ->
            val blog = blogPost.copy(title = title)
            setState { copy(blog = blog) }
        }
    }

    /**
     * Set blog description
     */
    private fun onUpdateDescription(description: String) {
        currentState.blog?.let { blogPost ->
            val blog = blogPost.copy(description = description)
            setState { copy(blog = blog) }
        }
    }

    /**
     * Set blog uri
     */
    private fun onUpdateOriginalUri(uri: Uri?) {
        setState {
            copy(originalUri = uri)
        }
    }

    /**
     * Start fetching blog
     */
    private fun onBlogObtained(blogPk: Int?) {
        viewModelScope.launch {
            blogPk?.let { pk ->
                getBlogUseCase(pk).let {
                    val blog = blogDomainPresentationMapper.from(it)
                    setState {
                        copy(
                            blog = blog,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Start updating blog
     */
    private fun onUpdateBlog(blogPk: Int?, imageUri: Uri?) {
        val imageUpdateTime = dateFormatUseCase()
        val updateTime = if (imageUri == null) "" else imageUpdateTime
        setState { copy(loading = true) }
        job?.cancel()
        blogPk?.let { pk ->
            currentState.blog?.let {
                job = viewModelScope.launch {
                    val updateBlogResult =
                        updateBlogUseCase(
                            pk,
                            it.title,
                            it.description,
                            updateTime
                        )
                    when {
                        updateBlogResult.titleError != null -> setEffect {
                            ShowSnackBarError(
                                title_error_message
                            )
                        }
                        updateBlogResult.descriptionError != null -> setEffect {
                            ShowSnackBarError(
                                description_error_message
                            )
                        }
                        else -> {
                            when (updateBlogResult.result) {
                                is DataResult.Success -> {
                                    if (imageUri != null) {
                                        storageReference.getReference("image/$updateTime")
                                            .putFile(imageUri).addOnSuccessListener {
                                                setEffect { ShowToast }
                                                setEffect { NavigateUp }
                                            }
                                    } else {
                                        setEffect { ShowToast }
                                        setEffect { NavigateUp }
                                    }
                                }
                                is DataResult.Error -> {
                                    setEffect {
                                        ShowSnackBarError(
                                            (updateBlogResult.result as DataResult.Error<Unit>)
                                                .exception.getStringResId()
                                        )
                                    }
                                }
                                null -> return@launch
                            }
                        }
                    }
                }
            }
        }
    }
}
