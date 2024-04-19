package com.mohamedashraf.notes.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class ToolbarFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ToolbarManager(buildFragmentToolbar(), view).prepareToolbar()
    }

    protected abstract fun buildFragmentToolbar(): FragmentToolbar
}