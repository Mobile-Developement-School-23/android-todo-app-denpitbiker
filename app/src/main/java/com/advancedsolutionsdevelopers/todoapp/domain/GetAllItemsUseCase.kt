package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class GetAllItemsUseCase {
    suspend operator fun invoke(): Flow<List<TodoItem>> = TodoItemsRepository.database.toDoItemDao().getAll()
}