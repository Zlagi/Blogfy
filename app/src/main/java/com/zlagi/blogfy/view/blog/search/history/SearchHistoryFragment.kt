package com.zlagi.blogfy.view.blog.search.history

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.zlagi.blogfy.databinding.FragmentSearchBlogBinding
import com.zlagi.blogfy.view.utils.collectWhenStarted
import com.zlagi.blogfy.view.utils.hideKeyboard
import com.zlagi.presentation.viewmodel.blog.search.historyview.QUERY
import com.zlagi.presentation.viewmodel.blog.search.historyview.SearchHistoryContract
import com.zlagi.presentation.viewmodel.blog.search.historyview.SearchHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchHistoryFragment : Fragment() {

    private val viewModel by viewModels<SearchHistoryViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentSearchBlogBinding? = null

    private var searchHistoryAdapter: SearchHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setEvent(SearchHistoryContract.SearchHistoryEvent.LoadHistory)
        searchHistoryAdapter = createSearchHistoryAdapter()
        setupHistoryRecyclerView()
        setupViews()
        navigateUp()
        observeViewState()
        observeViewEffect()
    }

    private fun createSearchHistoryAdapter(): SearchHistoryAdapter {
        return SearchHistoryAdapter { action, search ->
            when (action) {
                SearchAction.DELETE -> {
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.DeleteHistoryItem(
                            query = search.query
                        )
                    )
                }
                SearchAction.GET -> {
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.UpdateFocusState(
                            state = true
                        )
                    )
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.HistoryItemClicked(
                            query = search.query
                        )
                    )
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.NavigateTo
                    )
                }
            }
        }
    }

    private fun setupViews() {
        binding.apply {
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
                QUERY
            )?.observe(viewLifecycleOwner) { query ->
                query?.run {
                    binding.searchInputText.setText(query)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        QUERY,
                        null
                    )
                }
            }
            searchHistory.clearSearchHistory.setOnClickListener {
                viewModel.setEvent(SearchHistoryContract.SearchHistoryEvent.ClearHistory)
            }
            searchInputText.setOnFocusChangeListener { view, _ ->
                if (view.hasFocus()) {
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.UpdateFocusState(
                            state = true
                        )
                    )
                }
            }
            searchInputText.addTextChangedListener {
                viewModel.setEvent(SearchHistoryContract.SearchHistoryEvent.UpdateQuery(query = it.toString()))
            }
            searchInputText.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = view.text.toString()
                    if (query.isNotEmpty()) {
                        viewModel.setEvent(
                            SearchHistoryContract.SearchHistoryEvent.NavigateTo
                        )
                    }
                }
                true
            }
        }
    }

    private fun navigateUp() {
        binding.apply {
            searchBackButton.setOnClickListener {
                requireActivity().hideKeyboard()
                binding.searchInputText.clearFocus()
                viewModel.setEvent(SearchHistoryContract.SearchHistoryEvent.NavigateUp)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.searchInputText.clearFocus()
            viewModel.setEvent(SearchHistoryContract.SearchHistoryEvent.NavigateUp)
        }
    }

    private fun setupHistoryRecyclerView() {
        binding.searchHistory.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) { state ->
            render(state)
        }
    }

    private fun render(state: SearchHistoryContract.SearchHistoryViewState) {
        binding.apply {
            searchHistory.apply {
                initialSearchMessage.isVisible = state.emptyHistory
                historyRecyclerView.isVisible = !state.emptyHistory
                clearSearchHistory.isVisible = !state.emptyHistory
            }
            appbar.setExpanded(state.expansion)
            searchBackButton.setImageResource(state.icon)
            searchHistoryAdapter?.submitList(state.data)
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(
        effect: SearchHistoryContract.SearchHistoryViewEffect
    ) {
        binding.apply {
            when (effect) {
                is SearchHistoryContract.SearchHistoryViewEffect.NavigateUp -> {
                    searchInputText.text = null
                    viewModel.setEvent(
                        SearchHistoryContract.SearchHistoryEvent.UpdateFocusState(
                            state = false
                        )
                    )
                }
                is SearchHistoryContract.SearchHistoryViewEffect.NavigateTo -> {
                    requireActivity().hideKeyboard()
                    val action =
                        SearchHistoryFragmentDirections.actionSearchHistoryFragmentToSearchResultFragment(
                            effect.query
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }
}
