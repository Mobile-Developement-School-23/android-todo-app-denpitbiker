package com.advancedsolutionsdevelopers.todoapp.domain

import com.advancedsolutionsdevelopers.todoapp.data.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNumberOfCompletedUseCase @Inject constructor(private val dao: ToDoItemDao) {
    operator fun invoke(): Flow<Int> = dao.getNumOfCompleted()
}