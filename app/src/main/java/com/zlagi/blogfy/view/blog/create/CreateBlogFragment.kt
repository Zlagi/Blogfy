package com.zlagi.blogfy.view.blog.create

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.load
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.transition.MaterialFadeThrough
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentCreateBlogBinding
import com.zlagi.blogfy.view.utils.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract.CreateBlogEvent.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogContract.CreateBlogViewEffect.*
import com.zlagi.presentation.viewmodel.blog.create.CreateBlogViewModel
import com.zlagi.presentation.viewmodel.blog.feed.SHOULD_REFRESH
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateBlogFragment : Fragment() {

    private val viewModel by viewModels<CreateBlogViewModel>()

    private var _binding: FragmentCreateBlogBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: LoadingDialogFragment? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    private var imageUri: Uri? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            viewModel.setEvent(OriginalUriChanged(CropImage.getActivityResult(intent)?.originalUri))
            imageUri = CropImage.getActivityResult(intent)?.uriContent
            return CropImage.getActivityResult(intent)?.getUriFilePath(requireContext())?.toUri()
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewState()
        observeViewEffect()
    }

    private fun setupUI() {
        setupTitleTextField()
        setupDescriptionTextField()
        setupUri()
        clickConfirmCreateButton()
        clickCancelCreateButton()
        pressBackButton()
    }

    private fun setupTitleTextField() {
        binding.createBlogTitleInputText.doAfterTextChanged {
            viewModel.setEvent(TitleChanged(it!!.toString()))
        }
    }

    private fun setupDescriptionTextField() {
        binding.createBlogDescriptionInputText.doAfterTextChanged {
            viewModel.setEvent(DescriptionChanged(it!!.toString()))
        }
    }

    private fun setupUri() {
        binding.blogImageView.setOnClickListener {
            if (isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }
    }

    private fun clickConfirmCreateButton() {
        binding.confirmCreateButton.setOnClickListener {
            viewModel.setEvent(ConfirmCreateButtonClicked(imageUri))
            requireActivity().hideKeyboard()
        }
    }

    private fun clickCancelCreateButton() {
        binding.cancelCreateButton.setOnClickListener {
            requireActivity().hideKeyboard()
            viewModel.setEvent(CancelCreateButtonClicked)
        }
    }

    private fun pressBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().hideKeyboard()
            viewModel.setEvent(CancelCreateButtonClicked)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
            render(it)
        }
    }

    private fun render(state: CreateBlogContract.CreateBlogViewState) {
        showLoadingDialog(state.loading)
        binding.apply {
            setImage(state.originalUri)
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

    private fun setImage(
        uri: Uri?
    ) {
        val lottieDrawable = LottieDrawable()
        LottieCompositionFactory.fromRawRes(
            requireContext(),
            R.raw.image_loader
        ).addListener { lottieComposition ->
            lottieDrawable.composition = lottieComposition
            lottieDrawable.repeatCount = LottieDrawable.INFINITE
            lottieDrawable.playAnimation()
        }
        binding.blogImageView.apply {
            transitionName = uri?.toString()
            uri?.let {
                load(it, imageLoader) {
                    placeholder(lottieDrawable)
                }
            } ?: load(R.drawable.ic_baseline_add_photo_alternate_24, imageLoader) {
                placeholder(lottieDrawable)
            }
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(effect: CreateBlogContract.CreateBlogViewEffect) {
        when (effect) {
            is ShowToast -> showToast(R.string.created)
            is ShowSnackBarError -> showSnackBar(effect.message, LENGTH_SHORT)
            is ShowDiscardChangesDialog -> showDiscardChangesDialog()
            is NavigateUp -> navigateToFeed()
        }
    }

    private fun showDiscardChangesDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.discard_changes))
        builder.setMessage(getString(R.string.discard_changes_message))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            viewModel.setEvent(ConfirmDialogButtonClicked)
            dialog.cancel()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun navigateToFeed() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(SHOULD_REFRESH, true)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_LOADING_DIALOG = "loading_dialog"
    }
}
