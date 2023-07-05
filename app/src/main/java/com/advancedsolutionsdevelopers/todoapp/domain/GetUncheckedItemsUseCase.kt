package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow

class GetUncheckedItemsUseCase(private val dao: ToDoItemDao) {
    operator fun invoke(): Flow<List<TodoItem>> = dao.getAllUncompleted()
}