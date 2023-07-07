package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.advancedsolutionsdevelopers.todoapp.di.MainActivityScope
import com.advancedsolutionsdevelopers.todoapp.domain.ChangeConnectionModeUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetAllItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetNumberOfCompletedUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetServerCodesUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetUncheckedItemsUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SyncWithServerUseCase
import javax.inject.Inject

@MainActivityScope
class TasksListViewModelFactory @Inject constructor(
    private val deleteItemUseCase: DeleteItemUseCase,
    private val saveItemUseCase: SaveItemUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getUncheckedItemsUseCase: GetUncheckedItemsUseCase,
    private val getNumOfCompletedUseCase: GetNumberOfCompletedUseCase,
    private val getServerCodesUseCase: GetServerCodesUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val changeConnectionModeUseCase: ChangeConnectionModeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TasksListViewModel(
        deleteItemUseCase,
        saveItemUseCase,
        getAllItemsUseCase,
        getUncheckedItemsUseCase,
        getNumOfCompletedUseCase,
        getServerCodesUseCase,
        syncWithServerUseCase,
        changeConnectionModeUseCase
    ) as T
}