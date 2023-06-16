package com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import java.time.LocalDate


class TaskViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val isCompleteCheckbox: CheckBox = itemView.findViewById(R.id.is_complete_checkbox)
    val taskTextTextview: TextView = itemView.findViewById(R.id.task_text_textview)
    val deadlineDataTextview: TextView = itemView.findViewById(R.id.deadline_date_textview)
    fun onBind(task: TodoItem, curDate: LocalDate) {
        taskTextTextview.setTextColor(
            HandyFunctions.getThemeAttrColor(
                itemView.context,
                if (task.isCompleted) R.attr.support_separator_color else R.attr.label_primary
            )
        )
        if (task.isCompleted) {
            isCompleteCheckbox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                HandyFunctions.getThemeAttrColor(itemView.context, R.attr.base_green),
                PorterDuff.Mode.SRC_IN
            )
            taskTextTextview.paintFlags = taskTextTextview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            taskTextTextview.paintFlags =
                taskTextTextview.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            val isExpired =
                task.deadlineDate != null && task.deadlineDate!!.toEpochDay() < curDate.toEpochDay()
            isCompleteCheckbox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                HandyFunctions.getThemeAttrColor(
                    itemView.context,
                    if (isExpired) R.attr.color_delete_active else R.attr.support_separator_color
                ), PorterDuff.Mode.SRC_IN
            )
        }
    }
}