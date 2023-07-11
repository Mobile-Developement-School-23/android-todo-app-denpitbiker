package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    operator fun invoke(id: String): Flow<TodoItem?> = repository.getTaskById(id)
}