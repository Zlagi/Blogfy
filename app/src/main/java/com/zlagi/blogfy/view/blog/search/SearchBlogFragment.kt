package com.zlagi.blogfy.view.blog.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.zlagi.blogfy.R
import com.zlagi.blogfy.R.dimen.dimen_16
import com.zlagi.blogfy.R.drawable.ic_arrow_left
import com.zlagi.blogfy.R.drawable.ic_search
import com.zlagi.blogfy.R.integer.reply_motion_duration_large
import com.zlagi.blogfy.databinding.FragmentSearchBlogBinding
import com.zlagi.blogfy.view.utils.*
import com.zlagi.presentation.viewmodel.blog.search.SearchBlogViewModel
import com.zlagi.presentation.viewmodel.blog.search.SearchContract
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchEvent.*
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchViewEffect
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchViewEffect.Navigate
import com.zlagi.presentation.viewmodel.blog.search.SearchContract.SearchViewEffect.ShowSnackBarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchBlogFragment : Fragment() {

    private val viewModel by activityViewModels<SearchBlogViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentSearchBlogBinding? = null

    private var searchSuggestionsAdapter: SearchSuggestionsAdapter? = null
    private var searchBlogAdapter: SearchBlogAdapter? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setEvent(Initialization)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(reply_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchSuggestionsAdapter = createSearchSuggestionsAdapter()
        searchBlogAdapter = createSearchBlogAdapter()
        prepareForSearch()
        setupSuggestionsRecyclerView()
        setupSearchBlogRecyclerView()
        validateSearchSuggestionsViewExpansion()
        validateSearchSuggestionsViewCollapsion()
        observeViewState()
        observeViewEffect()
    }

    private fun createSearchSuggestionsAdapter(): SearchSuggestionsAdapter {
        return SearchSuggestionsAdapter { searchSuggestion ->
            searchSuggestion.pk?.let {
                onDeleteSearchSuggestion(searchSuggestion.pk!!)
            }
            searchSuggestion.query?.let {
                binding.apply {
                    appbar.setExpanded(false)
                    searchInputText.setText(it)
                }
                setupSearchSuggestionsView(
                    searchSuggestionsRecyclerView = GONE,
                    clearSearchSuggestionsButton = GONE,
                    initialSearchMessageTextView = GONE
                )
                executeSearchQuery(it)
            }
        }
    }

    private fun createSearchBlogAdapter(): SearchBlogAdapter {
        return SearchBlogAdapter(imageLoader) { blogPk ->
            blogPk.value?.let {
                onBlogDetail(it)
            }
        }
    }

    private fun prepareForSearch() {
        setupSearchViewListener()
        setupSearchQuery()
    }

    private fun setupSuggestionsRecyclerView() {
        binding.suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchSuggestionsAdapter
        }
    }

    private fun setupSearchBlogRecyclerView() {
        binding.blogsRecyclerView.apply {
            addItemDecoration(BottomSpacingItemDecoration(resources.getDimensionPixelSize(dimen_16)))
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = SpringAddItemAnimator()
            adapter = searchBlogAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == searchBlogAdapter?.itemCount?.minus(1)
                        && !viewModel.isLoadingNextPage
                        && !viewModel.isLastPage
                    ) {
                        onNextPage()
                    }
                }
            })
        }
    }

    private fun setupSearchViewListener() {
        binding.apply {
            searchInputText.setOnFocusChangeListener { view, _ ->
                if (view.hasFocus()) {
                    setupSearchBlogResultView(
                        clearFocus = false,
                        noSearchResult = GONE,
                        searchBlogsRecyclerView = GONE
                    )
                    viewModel.setEvent(SearchTextFocusedChanged(true))
                    updateSearchTextFocusListenerViews()
                    setupNavigationIcon()
                    setupBackButton()
                }
            }
        }
    }

    private fun setupSearchQuery() {
        binding.searchInputText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == IME_ACTION_UNSPECIFIED || actionId == IME_ACTION_SEARCH) {
                val searchQuery = view.text.toString()
                if (searchQuery.isNotEmpty()) {
                    executeSearchQuery(searchQuery)
                }
            }
            true
        }
    }

    private fun executeSearchQuery(searchQuery: String) {
        onNewSearch(searchQuery)
        setupScreenState(
            expandSearchSuggestionsView = false,
            collapseSearchSuggestionsView = false,
            showSearchResultView = true,
            setSearchTextFocus = true
        )
    }

    private fun updateSearchTextFocusListenerViews() {
        binding.apply {
            appbar.setExpanded(false)
            searchBackButton.setImageResource(ic_arrow_left)
        }
        updateTextChangedListenerViews()
    }

    private fun updateTextChangedListenerViews() {
        binding.apply {
            searchInputText.addTextChangedListener {
                if (it?.isEmpty() == true) searchSuggestionClearButton.visibility = GONE
                else {
                    setupSearchBlogResultView(
                        clearFocus = false,
                        noSearchResult = GONE,
                        searchBlogsRecyclerView = GONE
                    )
                    setupSearchSuggestionsView(
                        searchSuggestionsRecyclerView = GONE,
                        clearSearchSuggestionsButton = GONE,
                        initialSearchMessageTextView = GONE
                    )
                    updateClearTextViews()
                }
            }
        }
    }

    private fun updateClearTextViews() {
        binding.apply {
            searchSuggestionClearButton.visibility = VISIBLE
            searchSuggestionClearButton.setOnClickListener {
                requireActivity().showKeyboard()
                searchInputText.requestFocus()
                searchInputText.text = null
                searchSuggestionClearButton.visibility = GONE
            }
        }
    }

    private fun setupNavigationIcon() {
        binding.searchBackButton.setOnClickListener {
            when {
                viewModel.currentState.searchBlogResultView -> onCollapseSearchSuggestionsView()
                viewModel.currentState.searchSuggestionViewCollapsed -> onExpandSearchSuggestionsView()
                (viewModel.currentState.searchSuggestionViewExpanded && viewModel.currentState.searchTextFocused) -> {
                    setupSearchBlogResultView(
                        clearFocus = true,
                        noSearchResult = GONE,
                        searchBlogsRecyclerView = GONE
                    )
                    onExpandSearchSuggestionsView()
                }
            }
        }
    }

    private fun setupBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            when {
                viewModel.currentState.searchBlogResultView -> onCollapseSearchSuggestionsView()
                viewModel.currentState.searchSuggestionViewCollapsed -> onExpandSearchSuggestionsView()
                (viewModel.currentState.searchSuggestionViewExpanded && viewModel.currentState.searchTextFocused) -> {
                    setupSearchBlogResultView(
                        clearFocus = true,
                        noSearchResult = GONE,
                        searchBlogsRecyclerView = GONE
                    )
                    onExpandSearchSuggestionsView()
                }
                else -> findNavController().navigateUp()
            }
        }
    }

    private fun onCollapseSearchSuggestionsView() {
        setupScreenState(
            expandSearchSuggestionsView = false,
            collapseSearchSuggestionsView = true,
            showSearchResultView = false,
            setSearchTextFocus = true
        )
        setupSearchSuggestionsView(
            searchSuggestionsRecyclerView = VISIBLE,
            clearSearchSuggestionsButton = VISIBLE,
            initialSearchMessageTextView = GONE
        )
        binding.searchInputText.text = null
        validateSearchSuggestionsViewCollapsion()
    }

    private fun validateSearchSuggestionsViewCollapsion() {
        if (viewModel.currentState.searchSuggestionViewCollapsed && !viewModel.currentState.searchBlogResultView) {
            binding.progressBar.visibility = GONE
            binding.searchSuggestionClearButton.visibility = GONE
            setupAppBar()
            setupSearchSuggestions()
            setupSearchBlogResultView(
                clearFocus = true,
                noSearchResult = GONE,
                searchBlogsRecyclerView = GONE
            )
        }
    }

    private fun onExpandSearchSuggestionsView() {
        setupScreenState(
            expandSearchSuggestionsView = true,
            collapseSearchSuggestionsView = false,
            showSearchResultView = false,
            setSearchTextFocus = false
        )
        validateSearchSuggestionsViewExpansion()
    }

    private fun setupScreenState(
        expandSearchSuggestionsView: Boolean,
        collapseSearchSuggestionsView: Boolean,
        showSearchResultView: Boolean,
        setSearchTextFocus: Boolean
    ) {
        requireActivity().hideKeyboard()
        viewModel.setEvent(SearchSuggestionViewExpandedChanged(expandSearchSuggestionsView))
        viewModel.setEvent(SearchSuggestionViewCollapsedChanged(collapseSearchSuggestionsView))
        viewModel.setEvent(SearchBlogResultViewChanged(showSearchResultView))
        viewModel.setEvent(SearchTextFocusedChanged(setSearchTextFocus))
    }

    private fun validateSearchSuggestionsViewExpansion() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) { state ->
            if (state.searchSuggestionViewExpanded && !state.searchSuggestionViewCollapsed) {
                viewLifecycleOwner.lifecycleScope.launch {
                    setupAppBar()
                    setupSearchSuggestions()
                    binding.apply {
                        searchBackButton.setImageResource(ic_search)
                        searchInputText.text = null
                        searchSuggestionClearButton.visibility = GONE
                    }
                }
            }
        }
    }

    private fun setupSearchSuggestions() {
        if (viewModel.currentState.searchSuggestions.isNotEmpty()) {
            setupSearchSuggestionsView(
                searchSuggestionsRecyclerView = VISIBLE,
                clearSearchSuggestionsButton = VISIBLE,
                initialSearchMessageTextView = GONE
            )
        } else setupSearchSuggestionsView(
            searchSuggestionsRecyclerView = GONE,
            clearSearchSuggestionsButton = GONE,
            initialSearchMessageTextView = VISIBLE
        )
    }

    private fun setupSearchSuggestionsView(
        searchSuggestionsRecyclerView: Int,
        clearSearchSuggestionsButton: Int,
        initialSearchMessageTextView: Int,
    ) {
        binding.apply {
            suggestionsRecyclerView.visibility = searchSuggestionsRecyclerView
            clearSearchSuggestions.visibility = clearSearchSuggestionsButton
            initialSearchMessage.visibility = initialSearchMessageTextView
            clickDeleteSearchSuggestionButton()
        }
    }

    private fun clickDeleteSearchSuggestionButton() {
        binding.clearSearchSuggestions.setOnClickListener {
            onDeleteAllSearchSuggestions()
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) { state ->
            render(state)
        }
    }

    private fun render(state: SearchContract.SearchViewState) {
        if (state.searchSuggestions.isEmpty()) setupSearchSuggestionsView(
            searchSuggestionsRecyclerView = GONE,
            clearSearchSuggestionsButton = GONE,
            initialSearchMessageTextView = VISIBLE
        )
        validateResultView(state.loading, state.noSearchResults)
        binding.apply {
            progressBar.isVisible = state.loading
            blogsRecyclerView.apply {
                searchSuggestionsAdapter?.submitList(state.searchSuggestions)
                searchBlogAdapter?.submitList(state.blogs)
            }
        }
    }

    private fun validateResultView(loading: Boolean, noSearchResults: Boolean) {
        if (!viewModel.currentState.searchSuggestionViewCollapsed && viewModel.currentState.searchBlogResultView) {
            disableExpandingToolbar()
            setupSearchSuggestionsView(
                searchSuggestionsRecyclerView = GONE,
                clearSearchSuggestionsButton = GONE,
                initialSearchMessageTextView = GONE
            )
            if (!loading) {
                if (noSearchResults) setupSearchBlogResultView(
                    clearFocus = true,
                    noSearchResult = VISIBLE,
                    searchBlogsRecyclerView = GONE
                )
                else setupSearchBlogResultView(
                    clearFocus = true,
                    noSearchResult = GONE,
                    searchBlogsRecyclerView = VISIBLE
                )
            }
            setupAppBar()
        }
    }

    private fun disableExpandingToolbar() {
        binding.apply {
            ViewCompat.setNestedScrollingEnabled(blogsRecyclerView, false)
            val params = appbar.layoutParams as CoordinatorLayout.LayoutParams
            if (params.behavior == null)
                params.behavior = AppBarLayout.Behavior()
            val behaviour = params.behavior as AppBarLayout.Behavior
            behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })
        }
    }

    private fun setupSearchBlogResultView(
        clearFocus: Boolean,
        noSearchResult: Int,
        searchBlogsRecyclerView: Int
    ) {
        binding.apply {
            if (clearFocus) searchInputText.clearFocus()
            errorMessageTextView.visibility = noSearchResult
            blogsRecyclerView.visibility = searchBlogsRecyclerView
        }
    }

    private fun setupAppBar() {
        if (viewModel.currentState.searchTextFocused) binding.appbar.setExpanded(false)
        else binding.appbar.setExpanded(true)
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(
        effect: SearchViewEffect
    ) = when (effect) {
        is Navigate -> navigateToBlogDetail(effect.blogPk)
        is ShowSnackBarError -> {
            binding.progressBar.visibility = GONE
            showSnackBar(effect.message, LENGTH_LONG)
        }
    }

    private fun navigateToBlogDetail(blogPk: Int) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(reply_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(reply_motion_duration_large).toLong()
        }
        val bundle = bundleOf("blogPostPk" to blogPk)
        val action = R.id.action_searchFragment_to_nav_blog_detail
        findNavController().navigate(action, bundle)
    }

    private fun onNewSearch(searchQuery: String) {
        viewModel.setEvent(NewSearch(searchQuery))
    }

    private fun onBlogDetail(blogPk: Int) {
        viewModel.setEvent(BlogItemClicked(blogPk))
    }

    private fun onNextPage() {
        viewModel.setEvent(NextPage)
    }

    private fun onDeleteSearchSuggestion(searchSuggestionPk: Int) {
        viewModel.setEvent(DeleteSearchSuggestionButtonClicked(searchSuggestionPk))
    }

    private fun onDeleteAllSearchSuggestions() {
        viewModel.setEvent(DeleteAllSearchSuggestionsButtonClicked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchSuggestionsAdapter = null
        searchBlogAdapter = null
        _binding = null
    }
}
