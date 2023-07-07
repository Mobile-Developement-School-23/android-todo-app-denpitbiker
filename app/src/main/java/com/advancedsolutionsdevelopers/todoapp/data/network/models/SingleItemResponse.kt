package com.advancedsolutionsdevelopers.todoapp.data.network.models

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import kotlinx.serialization.Serializable

//обертка для обмена с сервером единичными задачами
@Serializable
data class SingleItemResponse(
    var element: TodoItem,
    var status: String? = null,
    var revision: Int? = null
)
