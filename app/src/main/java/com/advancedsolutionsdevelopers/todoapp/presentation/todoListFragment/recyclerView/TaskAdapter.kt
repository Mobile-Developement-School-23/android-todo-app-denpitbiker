package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.advancedsolutionsdevelopers.todoapp.R


class TaskAdapter :
    ListAdapter<ToDoItemUIState, TaskViewHolder>(TasksDiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = currentList[position]
        holder.itemView.setOnClickListener {
            item.onClick()
        }
        if (currentList[position].todoItem.id.isEmpty()) {
            return
        }
        holder.onBind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].todoItem.id.isEmpty()) R.layout.rv_end_button else R.layout.rv_item
    }
}