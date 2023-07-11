package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import androidx.lifecycle.ViewModel
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getItemUseCase: GetItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val saveItemUseCase: SaveItemUseCase
) :
    ViewModel() {
    fun getItem(id: String): Flow<TodoItem?> = getItemUseCase(id)
    fun deleteItem(todoItem: TodoItem) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteItemUseCase(todoItem)
        }

    }

    fun saveItem(todoItem: TodoItem, isEditMode: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            saveItemUseCase(todoItem, isEditMode)
        }
    }
}