package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TaskViewModel: ViewModel() {
    suspend fun getItem(id:String): Flow<TodoItem?> = TodoItemsRepository.getTaskById(id)
    fun deleteItem(todoItem: TodoItem){
        viewModelScope.launch(Dispatchers.IO) {
            TodoItemsRepository.deleteTask(todoItem)
        }

    }
    fun saveItem(todoItem: TodoItem,isEdited:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            if(isEdited){
                TodoItemsRepository.updateTask(todoItem)
            }else{
                TodoItemsRepository.addTask(todoItem)
            }

        }
    }
}