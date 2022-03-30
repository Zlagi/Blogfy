package com.zlagi.blogfy.view.blog.update

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.load
import com.airbnb.lottie.LottieCompositionFactory.fromRawRes
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.R.raw.image_loader
import com.zlagi.blogfy.databinding.FragmentUpdateBlogBinding
import com.zlagi.blogfy.view.utils.*
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogEvent.*
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogViewEffect
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogViewEffect.*
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogContract.UpdateBlogViewState
import com.zlagi.presentation.viewmodel.blog.update.UpdateBlogViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosariopfernandes.firecoil.load
import javax.inject.Inject

@AndroidEntryPoint
class UpdateBlogFragment : Fragment() {

    private val viewModel by viewModels<UpdateBlogViewModel>()

    private var _binding: FragmentUpdateBlogBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: LoadingDialogFragment? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    private var imageUri: Uri? = null
    private val storageReference = FirebaseStorage.getInstance()

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            imageUri = CropImage.getActivityResult(intent)?.uriContent
            viewModel.setEvent(OriginalUriChanged(CropImage.getActivityResult(intent)?.originalUri))
            return CropImage.getActivityResult(intent)?.getUriFilePath(requireContext())?.toUri()
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialization()

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewState()
        observeViewEffect()
    }

    private fun initialization() {
        viewModel.setEvent(Initialization)
    }

    private fun setupUI() {
        setupUri()
        clickConfirmUpdateButton()
        clickCancelUpdateButton()
        pressBackButton()
    }


    private fun setupUri() {
        binding.blogImageView.setOnClickListener {
            if (isStoragePermissionGranted()) {
                cropActivityResultLauncher.launch(null)
            }
        }
    }

    private fun clickConfirmUpdateButton() {
        binding.apply {
            confirmUpdateBlogButton.setOnClickListener {
                val title = blogTitleInputText.text.toString()
                val description = blogDescriptionInputText.text.toString()
                viewModel.setEvent(TitleChanged(title))
                viewModel.setEvent(DescriptionChanged(description))
                viewModel.setEvent(ConfirmUpdateButtonClicked(imageUri))
                requireActivity().hideKeyboard()
            }
        }
    }

    private fun clickCancelUpdateButton() {
        binding.cancelUpdateBlogButton.setOnClickListener {
            viewModel.setEvent(CancelUpdateButtonClicked)
        }
    }

    private fun pressBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().hideKeyboard()
            viewModel.setEvent(CancelUpdateButtonClicked)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
            render(it)
        }
    }

    private fun render(state: UpdateBlogViewState) {
        showLoadingDialog(state.loading)
        binding.apply {
            val lottieDrawable = LottieDrawable()
            fromRawRes(
                requireContext(),
                image_loader
            ).addListener { lottieComposition ->
                lottieDrawable.composition = lottieComposition
                lottieDrawable.repeatCount = INFINITE
                lottieDrawable.playAnimation()
            }
            blogImageView.apply {
                transitionName = state.blog?.pk.toString()
                state.originalUri?.let {
                    load(it, imageLoader) {
                        placeholder(lottieDrawable)
                    }
                } ?: if (state.blog?.updated?.isNotEmpty() == true) load(
                    storageReference.getReferenceFromUrl(
                        "gs://blogfy-e5b41.appspot.com/image/${state.blog?.updated}"
                    )
                ) {
                    placeholder(lottieDrawable)
                }
                else load(storageReference.getReferenceFromUrl("gs://blogfy-e5b41.appspot.com/image/${state.blog?.created}")) {
                    placeholder(lottieDrawable)
                }
            }
            blogTitleInputText.setText(state.blog?.title)
            blogDescriptionInputText.setText(state.blog?.description)
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
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(effect: UpdateBlogViewEffect) {
        when (effect) {
            is ShowToast -> showToast(R.string.updated)
            is ShowSnackBarError -> showSnackBar(effect.message, LENGTH_SHORT)
            is ShowDiscardChangesDialog -> showDiscardChangesDialog()
            is NavigateUp -> navigateUp()
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

    private fun navigateUp() {
        requireActivity().hideKeyboard()
        findNavController().navigateUp()
    }

    private fun cacheState() {
        val title = binding.blogTitleInputText.text.toString()
        val description = binding.blogDescriptionInputText.text.toString()
        viewModel.setEvent(TitleChanged(title))
        viewModel.setEvent(DescriptionChanged(description))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_LOADING_DIALOG = "loading_dialog"
    }
}
