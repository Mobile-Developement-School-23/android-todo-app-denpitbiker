package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import javax.inject.Inject


class DeleteItemUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    suspend operator fun invoke(todoItem: TodoItem) {
        repository.deleteTask(todoItem)
    }
}