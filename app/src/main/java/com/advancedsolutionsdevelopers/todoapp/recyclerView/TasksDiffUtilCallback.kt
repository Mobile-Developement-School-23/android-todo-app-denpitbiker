package com.advancedsolutionsdevelopers.todoapp.recyclerView

import androidx.recyclerview.widget.DiffUtil

class TasksDiffUtilCallback(
    private var oldList: List<TodoItem>,
    private var newList: List<TodoItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].text == newList[newItemPosition].text &&
                oldList[oldItemPosition].isCompleted == newList[newItemPosition].isCompleted &&
                oldList[oldItemPosition].deadlineDate == newList[newItemPosition].deadlineDate)

    }
}