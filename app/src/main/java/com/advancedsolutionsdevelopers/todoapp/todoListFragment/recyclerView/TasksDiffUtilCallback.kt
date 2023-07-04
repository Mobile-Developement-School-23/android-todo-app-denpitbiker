package com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem

//Используется для оптимального обновления recyclerview
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
                oldList[oldItemPosition].deadlineDate == newList[newItemPosition].deadlineDate &&
                oldList[oldItemPosition].priority == newList[newItemPosition].priority)

    }
}