package com.advancedsolutionsdevelopers.todoapp.recyclerView

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R


class TaskViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val isCompleteCheckbox: CheckBox = itemView.findViewById(R.id.is_complete_checkbox)
    val taskTextTextview: TextView = itemView.findViewById(R.id.task_text_textview)
    val deadlineDataTextview: TextView = itemView.findViewById(R.id.deadline_date_textview)

}