package com.zlagi.presentation.viewmodel.blog.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zlagi.common.mapper.getStringResId
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.usecase.blog.checkauthor.CheckBlogAuthorUseCase
import com.zlagi.domain.usecase.blog.delete.DeleteBlogUseCase
import com.zlagi.domain.usecase.blog.detail.GetBlogUseCase
import com.zlagi.presentation.mapper.BlogDomainPresentationMapper
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.*
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.*
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogDetailViewModel @Inject constructor(
    private val getBlogUseCase: GetBlogUseCase,
    private val checkBlogAuthorUseCase: CheckBlogAuthorUseCase,
    private val deleteBlogUseCase: DeleteBlogUseCase,
    private val blogDomainPresentationMapper: BlogDomainPresentationMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentState: BlogDetailViewState
        get() = viewState.value

    val viewState: StateFlow<BlogDetailViewState> get() = _viewState
    private val _viewState = MutableStateFlow(BlogDetailViewState())

    private val _viewEffect: Channel<BlogDetailViewEffect> = Channel()
    val viewEffect = _viewEffect.receiveAsFlow()

    private var job: Job? = null

    var blogPk = savedStateHandle.get<Int>("blogPostPk")

    /**
     * Set new ui state
     */
    private fun setState(reduce: BlogDetailViewState.() -> BlogDetailViewState) {
        val newState = viewState.value.reduce()
        _viewState.value = newState
    }

    /**
     * Set new effect
     */
    private fun setEffect(builder: () -> BlogDetailViewEffect) {
        val effectValue = builder()
        setState { copy(loading = false) }
        viewModelScope.launch { _viewEffect.send(effectValue) }
    }

    /**
     * Handle events
     */
    fun setEvent(event: BlogDetailEvent) {
        when (event) {
            is Initialization -> onBlogObtained(blogPk)
            is CheckBlogAuthor -> onCheckBlogAuthor()
            is UpdateBlogButtonClicked -> setEffect {
                NavigateToUpdateBlog(
                    pk = blogPk,
                    title = currentState.blog?.title,
                    description = currentState.blog?.description
                )
            }
            is DeleteBlogButtonClicked -> setEffect { ShowDeleteBlogDialog }
            is ConfirmDialogButtonClicked -> onDeleteBlog()
            is RefreshData -> onBlogObtained(blogPk = currentState.blog?.pk!!)
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
                    setState { copy(blog = blog, loading = false) }
                }
            }
        }
    }

    /**
     * Start checking author
     */
    private fun onCheckBlogAuthor() {
        job?.cancel()
        blogPk?.let {
            job = viewModelScope.launch {
                when (val result = checkBlogAuthorUseCase(it)) {
                    is DataResult.Success -> setState {
                        copy(
                            loading = false,
                            isAuthor = result.data
                        )
                    }
                    is DataResult.Error -> setEffect {
                        ShowSnackBarError(result.exception.getStringResId())
                    }
                }
            }
        }
    }

    /**
     * Start deleting blog
     */
    private fun onDeleteBlog() {
        job?.cancel()
        blogPk?.let {
            job = viewModelScope.launch {
                setState { copy(loading = true) }
                when (val result = deleteBlogUseCase(it)) {
                    is DataResult.Success -> setEffect { NavigateUp(true) }
                    is DataResult.Error -> setEffect { ShowSnackBarError(result.exception.getStringResId()) }
                }
            }
        }
    }
}
