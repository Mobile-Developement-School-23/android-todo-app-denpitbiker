package com.advancedsolutionsdevelopers.todoapp.network.models

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import kotlinx.serialization.Serializable

@Serializable
data class SingleItemResponse(
    var element: TodoItem,
    var status: String? = null,
    var revision: Int? = null
)
