package com.mohamedashraf.notes.ui.fragments

import android.view.MenuItem
import java.lang.IllegalArgumentException

class FragmentToolbar private constructor(
    val resId: Int,
    val title: Int,
    val menuId: Int,
    val menuItems: MutableList<Int>,
    val menuClicks: MutableList<MenuItem.OnMenuItemClickListener?>
) {
    companion object {
        const val NO_TOOLBAR = -1
    }

    class Builder {
        private var resId: Int = -1
        private var menuId: Int = -1
        private var title: Int = -1
        private var menuItems: MutableList<Int> = mutableListOf()
        private var menuClicks: MutableList<MenuItem.OnMenuItemClickListener?> = mutableListOf()

        fun withId(resId: Int) = apply { this.resId = resId }

        fun withTitle(title: Int) = apply { this.title = title }

        fun withMenu(menuId: Int) = apply { this.menuId = menuId }

        fun withMenuItems(menuItems: MutableList<Int>, menuClicks: MutableList<MenuItem.OnMenuItemClickListener?>) = apply {
            this.menuItems.addAll(menuItems)
            this.menuClicks.addAll(menuClicks)
        }
        fun build() : FragmentToolbar {

            if (resId == -1)
                throw IllegalArgumentException("You Have to Set Toolbar Resource ID")

            return FragmentToolbar(resId, title, menuId, menuItems, menuClicks)
        }
    }

}