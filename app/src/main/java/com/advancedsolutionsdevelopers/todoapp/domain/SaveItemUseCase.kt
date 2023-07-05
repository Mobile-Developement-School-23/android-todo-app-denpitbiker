package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository

class SaveItemUseCase(private val repository: TodoItemsRepository) {
    suspend operator fun invoke(todoItem: TodoItem, isEditMode: Boolean) {
        if (isEditMode) {
            repository.updateTask(todoItem)
        } else {
            repository.addTask(todoItem)
        }
    }
}