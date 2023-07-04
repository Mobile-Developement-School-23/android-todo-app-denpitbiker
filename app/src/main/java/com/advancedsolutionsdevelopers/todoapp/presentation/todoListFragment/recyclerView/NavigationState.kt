package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import android.os.Bundle

data class NavigationState(
    val bundle: Bundle? = null,
    val mode: NavigationMode = NavigationMode.None
)

enum class NavigationMode {
    None, Item
}