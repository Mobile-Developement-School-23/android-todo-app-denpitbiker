package com.advancedsolutionsdevelopers.todoapp.data.network.models

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import kotlinx.serialization.Serializable

//обертка для обмена с сервером массивами данных
@Serializable
data class ArrayResponse(
    var list: List<TodoItem>,
    var status: String? = null,
    var revision: Int? = null
)
