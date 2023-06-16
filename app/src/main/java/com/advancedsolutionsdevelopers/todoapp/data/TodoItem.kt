package com.advancedsolutionsdevelopers.todoapp.data

import java.time.LocalDate

data class TodoItem(
    val id: String,
    var text: String,
    var priority: Priority,
    var isCompleted: Boolean,
    var creationDate: LocalDate,
    var deadlineDate: LocalDate?,
    var lastEditDate: LocalDate?
)

