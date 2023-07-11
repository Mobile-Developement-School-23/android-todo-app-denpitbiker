package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerCodesUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    operator fun invoke(): Flow<Int> {
        return repository.codeChannel
    }
}