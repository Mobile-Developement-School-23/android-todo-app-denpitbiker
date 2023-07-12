package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.advancedsolutionsdevelopers.todoapp.di.MainActivityScope
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import javax.inject.Inject

@MainActivityScope
class TaskViewModelFactory @Inject constructor(
    private val getItemUseCase: GetItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val saveItemUseCase: SaveItemUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(
        getItemUseCase, deleteItemUseCase, saveItemUseCase
    ) as T
}