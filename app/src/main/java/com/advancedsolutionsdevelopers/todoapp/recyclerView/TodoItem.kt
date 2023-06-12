package com.advancedsolutionsdevelopers.todoapp.recyclerView

import java.time.LocalDate

//Что ты тут делаешь?)
data class TodoItem(
    val id: Long,
    var text: String,
    var priority: Byte,
    var isCompleted: Boolean,
    var creationDate: LocalDate,
    var deadlineDate: LocalDate?,
    var lastEditDate: LocalDate?
)
