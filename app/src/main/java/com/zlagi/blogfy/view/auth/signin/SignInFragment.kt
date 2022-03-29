package com.zlagi.blogfy.view.auth.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentSignInBinding
import com.zlagi.blogfy.view.auth.AuthActivity
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.collectWhenStarted
import com.zlagi.blogfy.view.utils.hideKeyboard
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInEvent.*
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewEffect
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewEffect.NavigateToFeed
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewEffect.ShowSnackBarError
import com.zlagi.presentation.viewmodel.auth.signin.SignInContract.SignInViewState
import com.zlagi.presentation.viewmodel.auth.signin.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel by viewModels<SignInViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentSignInBinding? = null

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(layoutInflater)
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
        setupPasswordTextField()
        clickEmailSignInButton()
        clickSignUpTextView()
    }

    private fun setupEmailTextField() {
        binding.emailInputText.doAfterTextChanged {
            viewModel.setEvent(EmailChanged(it!!.toString()))
        }
    }

    private fun setupPasswordTextField() {
        binding.passwordInputText.doAfterTextChanged {
            viewModel.setEvent(PasswordChanged(it!!.toString()))
        }
    }

    private fun clickEmailSignInButton() {
        binding.signInButton.setOnClickListener {
            hideSoftKeyboard()
            viewModel.setEvent(SignInButtonClicked)
        }
    }

    private fun clickSignUpTextView() {
        binding.signUpTextView.setOnClickListener {
            hideSoftKeyboard()
            viewModel.setEvent(SignUpTextViewClicked)
        }
    }

    private fun handleSignInButton(state: Boolean) {
        binding.signInButton.isEnabled = state
    }

    private fun hideSoftKeyboard() {
        requireActivity().hideKeyboard()
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
            render(it)
        }
    }

    private fun render(state: SignInViewState) {
        binding.apply {
            showLoadingDialog(state.loading)
            emailTextInputLayout.error = resources.getString(state.emailError)
            passwordTextInputLayout.error = resources.getString(state.passwordError)
        }
    }

    private fun showLoadingDialog(state: Boolean) {
        if (state) {
            handleSignInButton(false)
            if (loadingDialog == null) {
                loadingDialog = LoadingDialogFragment()
            }
            loadingDialog?.let {
                if (!it.isVisible) {
                    it.show(requireActivity().supportFragmentManager, TAG_LOADING_DIALOG)
                }
            }
        } else {
            handleSignInButton(true)
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    private fun observeViewEffect() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewEffect) {
            reactTo(it)
        }
    }

    private fun reactTo(effect: SignInViewEffect) {
        when (effect) {
            is ShowSnackBarError -> showSnackBarError(effect.message)
            is SignInViewEffect.NavigateToSignUp -> navigateToSignUp()
            is NavigateToFeed -> navigateToFeed()
        }
    }

    private fun showSnackBarError(message: Int) {
        showSnackBar(message, LENGTH_SHORT)
    }

    private fun navigateToSignUp() {
        val action = R.id.action_signInFragment_to_signUpFragment
        findNavController().navigate(action)
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
