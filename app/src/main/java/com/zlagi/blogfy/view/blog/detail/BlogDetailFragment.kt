package com.zlagi.blogfy.view.blog.detail

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.R.raw
import com.zlagi.blogfy.databinding.FragmentBlogDetailBinding
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.MenuBottomSheetDialogFragment
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.CheckBlogAuthor
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.ConfirmDialogButtonClicked
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.DeleteBlogButtonClicked
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.Initialization
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailEvent.UpdateBlogButtonClicked
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect.NavigateToUpdateBlog
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect.NavigateUp
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect.ShowDeleteBlogDialog
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewEffect.ShowSnackBarError
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailContract.BlogDetailViewState
import com.zlagi.presentation.viewmodel.blog.detail.BlogDetailViewModel
import com.zlagi.presentation.viewmodel.blog.feed.SHEET_DIALOG_ITEM
import com.zlagi.presentation.viewmodel.blog.feed.SHOULD_REFRESH
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosariopfernandes.firecoil.load
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BlogDetailFragment : Fragment() {

    private val viewModel by viewModels<BlogDetailViewModel>()

    private var _binding: FragmentBlogDetailBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: LoadingDialogFragment? = null

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private var alertDialogDisplayed = false

    private var menuBottomSheetDialogFragment: MenuBottomSheetDialogFragment? = null

    private var firestoreImageBucketUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        menuBottomSheetDialogFragment = createMenuBottomSheet()
        blogOptionsButtonClicked()
        observeViewState()
        observeViewEffect()
        onCheckBlogAuthor()
        onBackButtonClicked()

        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        firestoreImageBucketUrl = ai.metaData["firestoreImageBucketUrlKey"].toString()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            SHEET_DIALOG_ITEM
        )?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                when {
                    this == 1 -> {
                        findNavController().currentBackStackEntry?.savedStateHandle?.set(
                            SHEET_DIALOG_ITEM,
                            null
                        )
                        onUpdateBlog()
                    }
                    else -> {
                        findNavController().currentBackStackEntry?.savedStateHandle?.set(
                            SHEET_DIALOG_ITEM,
                            null
                        )
                        onDeleteBlog()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("AlertDialog", alertDialogDisplayed)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getBoolean("AlertDialog")?.let { state ->
            showDeleteBlogDialog(state)
        }
    }

    private fun initialization() {
        viewModel.setEvent(Initialization)
    }

    private fun createMenuBottomSheet(): MenuBottomSheetDialogFragment {
        return MenuBottomSheetDialogFragment()
    }

    private fun onUpdateBlog() {
        viewModel.setEvent(
            UpdateBlogButtonClicked
        )
    }

    private fun onDeleteBlog() {
        viewModel.setEvent(
            DeleteBlogButtonClicked
        )
    }

    private fun blogOptionsButtonClicked() {
        binding.blogOptionsImageButton.setOnClickListener {
            menuBottomSheetDialogFragment?.show(parentFragmentManager, null)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(STARTED) {
                viewModel.viewState.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(state: BlogDetailViewState) {
        showLoadingDialog(state.loading)
        binding.apply {
            val lottieDrawable = LottieDrawable()
            LottieCompositionFactory.fromRawRes(
                requireContext(),
                raw.image_loader
            ).addListener { lottieComposition ->
                lottieDrawable.composition = lottieComposition
                lottieDrawable.repeatCount = INFINITE
                lottieDrawable.playAnimation()
            }
            blogImageView.apply {
                transitionName = state.blog?.pk.toString()
                if (state.blog?.updated?.isNotEmpty() == true) {
                    load(
                        firebaseStorage.getReferenceFromUrl(
                            "$firestoreImageBucketUrl${state.blog?.updated}"
                        )
                    ) {
                        placeholder(lottieDrawable)
                    }
                } else {
                    load(
                        firebaseStorage.getReferenceFromUrl(
                            "$firestoreImageBucketUrl${state.blog?.created}"
                        )
                    ) {
                        placeholder(lottieDrawable)
                    }
                }
            }
            blogTitleTextView.text = state.blog?.title
            blogDescriptionTextView.apply {
                text = state.blog?.description
                visibility = VISIBLE
            }
            binding.blogOptionsImageButton.isVisible = state.isAuthor
        }
    }

    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialogFragment()
            }
            loadingDialog?.let {
                if (!it.isVisible) {
                    it.show(
                        requireActivity().supportFragmentManager,
                        TAG_LOADING_DIALOG
                    )
                }
            }
        } else {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(STARTED) {
                viewModel.viewEffect.collect { reactTo(it) }
            }
        }
    }

    private fun reactTo(effect: BlogDetailViewEffect) {
        when (effect) {
            is NavigateToUpdateBlog -> navigateToUpdateBlog(
                effect.pk,
                effect.title,
                effect.description
            )
            is ShowDeleteBlogDialog -> showDeleteBlogDialog(true)
            is NavigateUp -> navigateUp(refreshFeed = true)
            is ShowSnackBarError -> showSnackBarError(effect.message)
        }
    }

    private fun navigateToUpdateBlog(pk: Int?, title: String?, description: String?) {
        val action = BlogDetailFragmentDirections.actionBlogDetailFragmentToUpdateBlogFragment(
            pk!!,
            title!!,
            description!!
        )
        findNavController().navigate(action)
    }

    private fun showDeleteBlogDialog(state: Boolean) {
        if (state) {
            alertDialogDisplayed = true
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.confirm_delete_blog))
            builder.setMessage(getString(R.string.delete_blog_message))
            builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.setEvent(ConfirmDialogButtonClicked)
                alertDialogDisplayed = false
                dialog.cancel()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                alertDialogDisplayed = false
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun navigateUp(refreshFeed: Boolean) {
        if (refreshFeed) findNavController().previousBackStackEntry?.savedStateHandle?.set(
            SHOULD_REFRESH,
            true
        )
        findNavController().navigateUp()
    }

    private fun showSnackBarError(message: Int) {
        showSnackBar(message, LENGTH_SHORT)
    }

    private fun onCheckBlogAuthor() {
        viewModel.setEvent(CheckBlogAuthor)
    }

    private fun onBackButtonClicked() {
        binding.toolbar.setNavigationOnClickListener {
            navigateUp(refreshFeed = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        menuBottomSheetDialogFragment = null
    }

    companion object {
        private const val TAG_LOADING_DIALOG = "loading_dialog"
    }
}
