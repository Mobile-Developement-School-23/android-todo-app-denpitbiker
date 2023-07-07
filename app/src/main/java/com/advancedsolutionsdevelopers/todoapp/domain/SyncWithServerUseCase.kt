package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import javax.inject.Inject

class SyncWithServerUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    suspend operator fun invoke() = repository.syncWithServer()
}