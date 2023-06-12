package com.advancedsolutionsdevelopers.todoapp.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {
    var tasks = arrayListOf<TodoItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.rv_item,parent,false))
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(tasks[position])
    }
}