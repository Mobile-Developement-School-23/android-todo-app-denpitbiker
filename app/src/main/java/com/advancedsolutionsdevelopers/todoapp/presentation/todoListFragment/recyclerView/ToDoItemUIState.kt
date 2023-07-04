package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import com.advancedsolutionsdevelopers.todoapp.data.TodoItem

data class ToDoItemUIState(
    val todoItem: TodoItem,
    val onDelete: () -> Unit = {},
    val onCheck: () -> Unit = {},
    val onClick: ()-> Unit = {}
)