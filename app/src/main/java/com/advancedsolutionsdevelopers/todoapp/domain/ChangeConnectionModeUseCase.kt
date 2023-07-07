package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import javax.inject.Inject


class ChangeConnectionModeUseCase @Inject constructor(private val repository: TodoItemsRepository) {
    operator fun invoke(dropTable: Boolean = false) = repository.changeConnectionMode(dropTable)
}