package com.zlagi.blogfy.view.account.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentUpdatePasswordBinding
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.hideKeyboard
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.blogfy.view.utils.showToast
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordEvent.*
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordViewEffect
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordViewEffect.*
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordContract.UpdatePasswordViewState
import com.zlagi.presentation.viewmodel.account.update.UpdatePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private val viewModel by viewModels<UpdatePasswordViewModel>()

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewState()
        observeViewEffect()
    }

    private fun setupUI() {
        setupCurrentPasswordTextField()
        setupNewPasswordTextField()
        setupConfirmNewPasswordTextField()
        clickConfirmUpdateButton()
        clickCancelUpdateButton()
        pressBackButton()
    }

    private fun setupCurrentPasswordTextField() {
        binding.currentPasswordInputText.doAfterTextChanged {
            handleConfirmUpdateButton(true)
            viewModel.setEvent(CurrentPasswordChanged(it.toString()))
        }
    }

    private fun setupNewPasswordTextField() {
        binding.newPasswordInputText.doAfterTextChanged {
            handleConfirmUpdateButton(true)
            viewModel.setEvent(NewPasswordChanged(it.toString()))
        }
    }

    private fun setupConfirmNewPasswordTextField() {
        binding.confirmNewPasswordInputText.doAfterTextChanged {
            handleConfirmUpdateButton(true)
            viewModel.setEvent(ConfirmNewPassword(it.toString()))
        }
    }

    private fun clickConfirmUpdateButton() {
        binding.confirmUpdatePasswordButton.setOnClickListener {
            handleConfirmUpdateButton(false)
            hideSoftKeyboard()
            viewModel.setEvent(ConfirmUpdatePasswordButtonClicked)
        }
    }

    private fun clickCancelUpdateButton() {
        binding.cancelUpdatePasswordButton.setOnClickListener {
            hideSoftKeyboard()
            viewModel.setEvent(CancelUpdatePasswordButtonClicked)
        }
    }

    private fun pressBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().hideKeyboard()
            viewModel.setEvent(CancelUpdatePasswordButtonClicked)
        }
    }

    private fun handleConfirmUpdateButton(state: Boolean) {
        binding.confirmUpdatePasswordButton.isEnabled = state
    }

    private fun hideSoftKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(STARTED) {
                viewModel.viewState.collect { render(it) }
            }
        }
    }

    private fun render(state: UpdatePasswordViewState) {
        showLoadingDialog(state.loading)
        binding.apply {
            currentPasswordTextInputLayout.error = resources.getString(state.currentPasswordError)
            newPasswordInputTextLayout.error = resources.getString(state.newPasswordError)
            confirmNewPasswordInputTextLayout.error =
                resources.getString(state.confirmNewPasswordError)
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

    private fun reactTo(effect: UpdatePasswordViewEffect) {
        when (effect) {
            is ShowToast -> showToast(R.string.updated)
            is ShowSnackBarError -> showSnackBar(effect.message, LENGTH_SHORT)
            is ShowDiscardChangesDialog -> showDiscardChangesDialog()
            is NavigateUp -> navigateToAccountDetail()
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

    private fun navigateToAccountDetail() {
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
