package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow

class DeleteItemUseCase(private val repository: TodoItemsRepository) {
    suspend operator fun invoke(todoItem: TodoItem) {
        repository.deleteTask(todoItem)
    }
}