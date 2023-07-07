package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import javax.inject.Inject

class SaveItemUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    suspend operator fun invoke(todoItem: TodoItem, isEditMode: Boolean) {
        if (isEditMode) {
            repository.updateTask(todoItem)
        } else {
            repository.addTask(todoItem)
        }
    }
}