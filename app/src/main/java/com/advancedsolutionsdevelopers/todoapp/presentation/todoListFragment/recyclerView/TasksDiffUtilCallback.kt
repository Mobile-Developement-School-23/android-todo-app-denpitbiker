package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem

//Используется для оптимального обновления recyclerview
class TasksDiffUtilCallback : DiffUtil.ItemCallback<ToDoItemUIState>() {


    override fun areItemsTheSame(oldItem: ToDoItemUIState, newItem: ToDoItemUIState): Boolean {
        return oldItem.todoItem.id == newItem.todoItem.id
    }

    override fun areContentsTheSame(oldItem: ToDoItemUIState, newItem: ToDoItemUIState): Boolean {
        return (oldItem.todoItem.text == newItem.todoItem.text &&
                oldItem.todoItem.isCompleted == newItem.todoItem.isCompleted &&
                oldItem.todoItem.deadlineDate == newItem.todoItem.deadlineDate &&
                oldItem.todoItem.priority == newItem.todoItem.priority ||
                oldItem.todoItem.id == "" && newItem.todoItem.id == ""
                )
    }
}