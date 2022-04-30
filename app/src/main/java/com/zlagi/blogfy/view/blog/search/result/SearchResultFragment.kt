package com.zlagi.blogfy.view.blog.search.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.snackbar.Snackbar
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentSearchBlogBinding
import com.zlagi.blogfy.view.utils.*
import com.zlagi.presentation.viewmodel.blog.search.historyview.QUERY
import com.zlagi.presentation.viewmodel.blog.search.resultview.SearchResultContract
import com.zlagi.presentation.viewmodel.blog.search.resultview.SearchResultContract.SearchResultEvent
import com.zlagi.presentation.viewmodel.blog.search.resultview.SearchResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    private val viewModel by viewModels<SearchResultViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentSearchBlogBinding? = null

    private var searchResultAdapter: SearchResultAdapter? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    private val args: SearchResultFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) viewModel.setEvent(
            SearchResultEvent.ExecuteSearch(
                init = true,
                query = args.query
            )
        )
        savedInstanceState?.putBoolean("init", false)
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
        searchResultAdapter = createSearchBlogAdapter()
        binding.searchInputText.setText(args.query)
        setupSearchBlogRecyclerView()
        setupViews()
        observeViewState()
        observeViewEffect()
    }

    private fun createSearchBlogAdapter(): SearchResultAdapter {
        return SearchResultAdapter(imageLoader)
    }

    private fun setupBackButtons(state: Int) {
        binding.apply {
            searchBackButton.setImageResource(state)
            if (state == R.drawable.ic_arrow_left) {
                searchBackButton.setOnClickListener {
                    requireActivity().hideKeyboard()
                    binding.searchInputText.clearFocus()
                    viewModel.setEvent(SearchResultEvent.NavigateUp)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.searchInputText.clearFocus()
            viewModel.setEvent(SearchResultEvent.NavigateUp)
        }
    }

    private fun setupViews() {
        binding.apply {
            searchInputText.setOnFocusChangeListener { view, _ ->
                if (view.hasFocus()) {
                    viewModel.setEvent(
                        SearchResultEvent.UpdateFocusState(
                            state = true
                        )
                    )
                }
            }
            searchInputText.addTextChangedListener {
                viewModel.setEvent(SearchResultEvent.UpdateQuery(query = it.toString()))
            }
            searchInputText.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = view.text.toString()
                    if (query.isNotEmpty()) {
                        requireActivity().hideKeyboard()
                        searchInputText.clearFocus()
                        viewModel.setEvent(
                            SearchResultEvent.UpdateFocusState(
                                state = false
                            )
                        )
                        viewModel.setEvent(
                            SearchResultEvent.ExecuteSearch(
                                init = false,
                                query = query
                            )
                        )
                    }
                }
                true
            }
        }
    }

    private fun setupSearchBlogRecyclerView() {
        binding.searchResult.blogsRecyclerView.apply {
            addItemDecoration(BottomSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.dimen_16)))
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = SpringAddItemAnimator()
            adapter = searchResultAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == searchResultAdapter?.itemCount?.minus(1)
                        && !viewModel.isLoadingNextPage
                        && !viewModel.isLastPage
                    ) {
                        viewModel.setEvent(SearchResultEvent.NextPage)
                    }
                }
            })
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) { state ->
            render(state)
        }
    }

    private fun render(state: SearchResultContract.SearchResultViewState) {
        binding.apply {
            searchHistory.apply {
                initialSearchMessage.visibility = View.GONE
                historyRecyclerView.visibility = View.GONE
                clearSearchHistory.visibility = View.GONE
            }
            searchResult.apply {
                progressBar.isVisible = state.loading
                errorMessageTextView.isVisible = state.showEmptyBlogs
                blogsRecyclerView.visibility = View.VISIBLE
            }
            appbar.setExpanded(false)
            setupBackButtons(state.icon)
            searchResultAdapter?.submitList(state.blogs)
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(
        effect: SearchResultContract.SearchResultViewEffect
    ) {
        binding.apply {
            when (effect) {
                is SearchResultContract.SearchResultViewEffect.ShowSnackBarError -> {
                    progressBar.visibility = View.GONE
                    showSnackBar(effect.message, Snackbar.LENGTH_LONG)
                }
                is SearchResultContract.SearchResultViewEffect.NavigateUp -> {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        QUERY,
                        effect.query
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchResultAdapter = null
        _binding = null
    }
}
