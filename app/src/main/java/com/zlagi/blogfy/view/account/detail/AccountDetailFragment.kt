package com.zlagi.blogfy.view.account.detail

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.google.android.material.transition.MaterialFadeThrough
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentAccountDetailBinding
import com.zlagi.blogfy.view.MainActivity
import com.zlagi.blogfy.view.utils.LoadingDialogFragment
import com.zlagi.blogfy.view.utils.showSnackBar
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailEvent.*
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailViewEffect
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailViewEffect.*
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailContract.AccountDetailViewState
import com.zlagi.presentation.viewmodel.account.detail.AccountDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountDetailFragment : Fragment() {

    private val viewModel by activityViewModels<AccountDetailViewModel>()

    private val binding get() = _binding!!
    private var _binding: FragmentAccountDetailBinding? = null

    private var loadingDialog: LoadingDialogFragment? = null

    private var alertDialogDisplayed = false

    private lateinit var gso: GoogleSignInOptions

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }

        initialization()
        initializeGoogleSignInOptions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickPasswordButton()
        clickSignOutButton()
        observeViewState()
        observeViewEffect()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("AlertDialog", alertDialogDisplayed)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getBoolean("AlertDialog")?.let { state ->
            showSignOutDialog(state)
        }
    }

    private fun initialization() {
        viewModel.setEvent(Initialization)
    }

    private fun initializeGoogleSignInOptions() {
        val ai: ApplicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["firebaseKey"]
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(value.toString())
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun clickPasswordButton() {
        binding.updatePasswordTextView.setOnClickListener {
            viewModel.setEvent(UpdatePasswordButtonClicked)
        }
    }

    private fun clickSignOutButton() {
        binding.signOutButton.setOnClickListener {
            viewModel.setEvent(SignOutButtonClicked)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(state: AccountDetailViewState) {
        showLoadingDialog(state.isSigningOut)
        binding.apply {
            progressBar.isVisible = state.loading
            emailTextView.text = state.account?.email
            usernameTextView.text = (state.account?.username)
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
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewEffect.collect { reactTo(it) }
            }
        }
    }

    private fun reactTo(effect: AccountDetailViewEffect) {
        when (effect) {
            is ShowSnackBarError -> showSnackBar(effect.message, LENGTH_SHORT)
            is NavigateToUpdatePassword -> navigateToUpdatePassword()
            is ShowDiscardChangesDialog -> showSignOutDialog(true)
            is NavigateToAuth -> navigateToAuth()
        }
    }

    private fun navigateToUpdatePassword() {
        val action = R.id.action_accountDetail_to_updatePasswordFragment
        findNavController().navigate(action)
    }

    private fun showSignOutDialog(state: Boolean) {
        if (state) {
            alertDialogDisplayed = true
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.confirm_sign_out))
            builder.setMessage(getString(R.string.sign_out_confirmation_message))
            builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.setEvent(ConfirmDialogButtonClicked)
                alertDialogDisplayed = false
                dialog.cancel()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                alertDialogDisplayed = false
                dialog.cancel()
            }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun navigateToAuth() {
        googleSignInClient.signOut()
        (requireActivity() as MainActivity).navAuthActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_LOADING_DIALOG = "loading_dialog"
    }
}
