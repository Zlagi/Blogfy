package com.zlagi.blogfy.view.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.zlagi.blogfy.R
import com.zlagi.presentation.viewmodel.blog.feed.SHEET_DIALOG_ITEM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.menu_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById(R.id.navigation_view)
        navigationView.inflateMenu(R.menu.blog_detail)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_edit_blog -> {
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        SHEET_DIALOG_ITEM, 1)
                }
                R.id.action_delete_blog -> {
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        SHEET_DIALOG_ITEM, 2)
                }
            }
            dismiss()
            true
        }
    }
}
