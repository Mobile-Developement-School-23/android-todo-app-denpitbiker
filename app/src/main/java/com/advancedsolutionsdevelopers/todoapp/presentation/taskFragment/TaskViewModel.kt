package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import com.advancedsolutionsdevelopers.todoapp.domain.DeleteItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.GetItemUseCase
import com.advancedsolutionsdevelopers.todoapp.domain.SaveItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel :
    ViewModel() {
    /*private val getItemUseCase = GetItemUseCase(dao)
    private val deleteItemUseCase = DeleteItemUseCase(repository)
    private val saveItemUseCase = SaveItemUseCase(repository)*/
    suspend fun getItem(id: String): Flow<TodoItem?> = TodoItemsRepository.getTaskById(id)
    fun deleteItem(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.deleteTask(todoItem)
        }

    }

    fun saveItem(todoItem: TodoItem, isEditMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isEditMode) {
                TodoItemsRepository.updateTask(todoItem)
            } else {
                TodoItemsRepository.addTask(todoItem)
            }
        }
    }
}