package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow

class GetNumberOfCompletedUseCase(private val dao: ToDoItemDao) {
    operator fun invoke(): Flow<Int> = dao.getNumOfCompleted()
}