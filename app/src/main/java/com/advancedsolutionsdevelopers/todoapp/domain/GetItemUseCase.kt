package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow

class GetItemUseCase(private val dao: ToDoItemDao) {
    operator fun invoke(id: String): Flow<TodoItem?> = dao.getItemById(id)
}