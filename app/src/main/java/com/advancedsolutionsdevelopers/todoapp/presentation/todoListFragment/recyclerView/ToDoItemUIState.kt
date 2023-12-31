package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
//Обертка, позволяющая удобно обрабатывать события с элементами RV
data class ToDoItemUIState(
    val todoItem: TodoItem,
    val onDelete: () -> Unit = {},
    val onCheck: () -> Unit = {},
    val onClick: () -> Unit = {}
)