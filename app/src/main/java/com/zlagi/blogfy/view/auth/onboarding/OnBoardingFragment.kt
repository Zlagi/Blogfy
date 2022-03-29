package com.zlagi.blogfy.view.auth.onboarding

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentOnBoardingBinding
import com.zlagi.blogfy.view.auth.AuthActivity
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.collectWhenStarted
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.auth.onboarding.OnBoardingContract
import com.zlagi.presentation.viewmodel.auth.onboarding.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val viewModel by viewModels<OnBoardingViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentOnBoardingBinding? = null

    private var loadingDialog: LoadingDialogFragment? = null

    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeApp()
        checkAuth()
        setupUI()
        observeViewState()
        observeViewEffect()
    }

    private fun closeApp() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    private fun checkAuth() {
        if (viewModel.checkAuthenticationStatus()) navigateToFeed()
    }

    private fun setupUI() {
        initializeGoogleSignInButton()
        clickGoogleSignInButton()
        clickEmailSignInButton()
        launchGoogleSignIn()
    }

    private fun initializeGoogleSignInButton() {
        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["firebaseKey"]
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(value.toString())
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun launchGoogleSignIn() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) handleGoogleSignIn(it.data)
        }
    }

    private fun handleGoogleSignIn(data: Intent?) {
        data?.let {
            viewModel.setEvent(
                OnBoardingContract.OnBoardingEvent.GoogleSignInButtonClicked(
                    it
                )
            )
        }
    }

    private fun clickGoogleSignInButton() {
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun clickEmailSignInButton() {
        binding.continueWithEmailButton.setOnClickListener {
            viewModel.setEvent(OnBoardingContract.OnBoardingEvent.EmailSignInButtonClicked)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.collectWhenStarted(viewModel.viewState) {
            render(it)
        }
    }

    private fun render(state: OnBoardingContract.OnBoardingViewState) {
        binding.apply {
            showLoadingDialog(state.loading)
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

    private fun reactTo(effect: OnBoardingContract.OnBoardingViewEffect) {
        when (effect) {
            is OnBoardingContract.OnBoardingViewEffect.ShowSnackBarError -> showSnackBar(
                effect.message,
                Snackbar.LENGTH_SHORT
            )
            is OnBoardingContract.OnBoardingViewEffect.NavigateToSignIn -> navigateToSignIn()
            is OnBoardingContract.OnBoardingViewEffect.NavigateToFeed -> navigateToFeed()
        }
    }

    private fun navigateToSignIn() {
        val action = R.id.action_onBoardingFragment_to_signInFragment
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