package com.advancedsolutionsdevelopers.todoapp.recyclerView

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R


class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val isCompleteCheckbox = itemView.findViewById<CheckBox>(R.id.is_complete_checkbox)
    val taskTextTextview = itemView.findViewById<TextView>(R.id.task_text_textview)
    val deadlineDataTextview = itemView.findViewById<TextView>(R.id.deadline_date_textview)
    fun onBind(task: TodoItem){
        isCompleteCheckbox.isChecked = task.isCompleted
        taskTextTextview.text = task.text
        if(task.deadlineDate!=null)
            deadlineDataTextview.text = task.deadlineDate.toString()
        if(task.isCompleted) {
            taskTextTextview.paintFlags = taskTextTextview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            taskTextTextview.paintFlags = taskTextTextview.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}