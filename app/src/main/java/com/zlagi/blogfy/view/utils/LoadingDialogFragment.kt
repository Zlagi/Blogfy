package com.zlagi.blogfy.view.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FragmentLoadingDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingDialogFragment : AppCompatDialogFragment() {

    private var _binding: FragmentLoadingDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) dismiss()
        _binding = FragmentLoadingDialogBinding.inflate(LayoutInflater.from(context)).apply {
            textView.setText(R.string.dialog_loading)
        }
        return AlertDialog.Builder(requireActivity(), R.style.MyCustomTheme)
            .setView(binding.root)
            .setCancelable(true)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
