package com.zlagi.blogfy.view.blog.feed

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.R.id.action_create_blog
import com.zlagi.blogfy.databinding.FragmentFeedBinding
import com.zlagi.blogfy.view.utils.collectWhenStarted
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedEvent.*
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedViewEffect
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedViewEffect.Navigate
import com.zlagi.presentation.viewmodel.blog.feed.FeedContract.FeedViewEffect.ShowSnackBarError
import com.zlagi.presentation.viewmodel.blog.feed.FeedViewModel
import com.zlagi.presentation.viewmodel.blog.feed.SHOULD_REFRESH
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private val viewModel by viewModels<FeedViewModel>()

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private var feedAdapter: FeedAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        initialization()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedAdapter = createFeedAdapter()
        setupMenu()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewState()
        observeViewEffect()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            SHOULD_REFRESH
        )?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                viewModel.setEvent(SwipeRefresh)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    SHOULD_REFRESH,
                    null
                )
            }
        }
    }

    private fun initialization() {
        viewModel.setEvent(Initialization)
    }

    private fun createFeedAdapter(): FeedAdapter {
        return FeedAdapter(firebaseStorage) { _, selectedBlog ->
            onBlogDetail(selectedBlog.pk)
        }
    }

    private fun setupMenu() {
        with(binding.toolbar) {
            inflateMenu(R.menu.feed)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    action_create_blog -> {
                        (it.icon as AnimatedVectorDrawable).start()
                        onCreateBlog()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupRecyclerView() {
        binding.feedRecyclerView.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == feedAdapter?.itemCount?.minus(1)
                        && !viewModel.isLoadingNextPage
                        && !viewModel.isLastPage
                    ) {
                        onNextPage()
                    }
                }
            })
        }
    }

    private fun observeViewState() {
        binding.apply {
            viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
                render(it)
            }
        }
    }

    private fun render(state: FeedContract.FeedViewState) {
        binding.apply {
            swipeRefresh.isRefreshing = state.loading
            val createBlogIcon = toolbar.menu.getItem(0)
            (createBlogIcon.icon as AnimatedVectorDrawable).start()
            if (!state.loading) {
                (createBlogIcon.icon as AnimatedVectorDrawable).reset()
                if (state.noResults) {
                    setupFeedResultView(GONE, VISIBLE)
                } else {
                    feedAdapter?.submitList(state.results)
                    setupFeedResultView(VISIBLE, GONE)
                }
            }
        }
    }

    private fun setupFeedResultView(recycler: Int, noResultsView: Int) {
        binding.apply {
            feedRecyclerView.visibility = recycler
            noResultsAnimationView.visibility = noResultsView
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(effect: FeedViewEffect) = when (effect) {
        is Navigate -> onNavigation(effect.blogPk)
        is ShowSnackBarError -> {
            showSnackBar(
                effect.error,
                Snackbar.LENGTH_SHORT
            )
        }
    }

    private fun onNavigation(blogPk: Int?) {
        if (blogPk != null) navigateToBlogDetail(blogPk)
        else navigateToCreateBlog()
    }

    private fun navigateToBlogDetail(blogPk: Int?) {
        blogPk?.let {
            val bundle = bundleOf("blogPostPk" to it)
            val action = R.id.action_feedFragment_to_nav_blog_detail
            findNavController().navigate(action, bundle)
        }
    }

    private fun navigateToCreateBlog() {
        val action = R.id.action_feedFragment_to_createBlogFragment
        findNavController().navigate(action)
    }

    private fun onBlogDetail(blogPk: Int) {
        viewModel.setEvent(BlogItemClicked(blogPk))
    }

    private fun onCreateBlog() {
        viewModel.setEvent(CreateBlogButtonClicked)
    }

    private fun onNextPage() {
        viewModel.setEvent(NextPage)
    }

    override fun onRefresh() {
        viewModel.setEvent(SwipeRefresh)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        feedAdapter = null
        _binding = null
    }
}
