package com.advancedsolutionsdevelopers.todoapp.network.models

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import kotlinx.serialization.Serializable

@Serializable
data class ArrayResponse(
    var list: List<TodoItem>,
    var status: String? = null,
    var revision: Int? = null
)
