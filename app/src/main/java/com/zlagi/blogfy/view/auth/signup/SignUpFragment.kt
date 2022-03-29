package com.zlagi.blogfy.view.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zlagi.blogfy.databinding.FragmentSignUpBinding
import com.zlagi.blogfy.view.auth.AuthActivity
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.collectWhenStarted
import com.zlagi.blogfy.view.utils.hideKeyboard
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.auth.signup.SignUpContract
import com.zlagi.presentation.viewmodel.auth.signup.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel by viewModels<SignUpViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentSignUpBinding? = null

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewState()
        observeViewEffect()
    }

    private fun setupUI() {
        setupEmailTextField()
        setupUsernameTextField()
        setupPasswordTextField()
        setupConfirmPasswordTextField()
        clickSignUpButton()
    }

    private fun setupEmailTextField() {
        binding.emailInputText.doAfterTextChanged {
            viewModel.setEvent(SignUpContract.SignUpEvent.EmailChanged(it!!.toString()))
        }
    }

    private fun setupUsernameTextField() {
        binding.usernameInputText.doAfterTextChanged {
            viewModel.setEvent(SignUpContract.SignUpEvent.UsernameChanged(it!!.toString()))
        }
    }

    private fun setupPasswordTextField() {
        binding.passwordInputText.doAfterTextChanged {
            viewModel.setEvent(SignUpContract.SignUpEvent.PasswordChanged(it!!.toString()))
        }
    }

    private fun setupConfirmPasswordTextField() {
        binding.confirmPasswordInputText.doAfterTextChanged {
            viewModel.setEvent(SignUpContract.SignUpEvent.ConfirmPasswordChanged(it!!.toString()))
        }
    }

    private fun clickSignUpButton() {
        binding.signUpButton.setOnClickListener {
            hideSoftKeyboard()
            viewModel.setEvent(SignUpContract.SignUpEvent.SignUpButtonClicked)
        }
    }

    private fun handleSignUpButton(state: Boolean) {
        binding.signUpButton.isEnabled = state
    }

    private fun hideSoftKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
            render(it)
        }
    }

    private fun render(state: SignUpContract.SignUpViewState) {
        binding.apply {
            showLoadingDialog(state.loading)
            emailTextInputLayout.error = resources.getString(state.emailError)
            usernameTextInputLayout.error = resources.getString(state.usernameError)
            passwordTextInputLayout.error = resources.getString(state.passwordError)
            confirmPasswordTextInputLayout.error = resources.getString(state.confirmPasswordError)
        }
    }

    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            handleSignUpButton(false)
            if (loadingDialog == null) {
                loadingDialog = LoadingDialogFragment()
            }
            loadingDialog?.let {
                if (!it.isVisible) {
                    it.show(requireActivity().supportFragmentManager, TAG_LOADING_DIALOG)
                }
            }
        } else {
            handleSignUpButton(true)
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(effect: SignUpContract.SignUpViewEffect) {
        when (effect) {
            is SignUpContract.SignUpViewEffect.ShowSnackBarError -> showSnackBarError(effect.message)
            is SignUpContract.SignUpViewEffect.NavigateToFeed -> navigateToFeed()
        }
    }

    private fun showSnackBarError(message: Int) {
        showSnackBar(message, Snackbar.LENGTH_SHORT)
    }

    private fun navigateToFeed() {
        (requireActivity() as AuthActivity).navMainActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_LOADING_DIALOG = "loading_dialog"
    }
}
