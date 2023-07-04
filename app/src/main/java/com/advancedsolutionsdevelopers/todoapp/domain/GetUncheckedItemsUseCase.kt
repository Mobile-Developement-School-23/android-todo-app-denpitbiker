package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.flow.Flow

class GetUncheckedItemsUseCase {
    suspend operator fun invoke(): Flow<List<TodoItem>> =
        TodoItemsRepository.database.toDoItemDao().getAllUncompleted()
}