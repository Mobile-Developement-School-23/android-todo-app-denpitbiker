package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.models.Priority
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import java.time.LocalDateTime


class TaskViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val converter = TimeFormatConverters()
    private val curDate by lazy { converter.dateToTimestamp(LocalDateTime.now(converter.zoneId)) }
    val isCompleteChBox: CheckBox = itemView.findViewById(R.id.is_complete_checkbox)
    private val taskTextTV: TextView = itemView.findViewById(R.id.task_text_textview)
    private val deadlineDataTV: TextView = itemView.findViewById(R.id.deadline_date_textview)
    fun onBind(item: ToDoItemUIState) {
        val task = item.todoItem
        setUpTextViews(task)
        setUpCheckbox(item)
        setCheckboxColor(task)
        setUpTextView(task)
    }

    private fun setUpTextView(task: TodoItem) {
        taskTextTV.setTextColor(
            getThemeAttrColor(
                itemView.context,
                if (task.isCompleted) R.attr.support_separator_color else R.attr.label_primary
            )
        )
        if (task.isCompleted) {

            taskTextTV.paintFlags = taskTextTV.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            taskTextTV.paintFlags =
                taskTextTV.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun setCheckboxColor(task: TodoItem) {
        if (task.isCompleted) {
            isCompleteChBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                getThemeAttrColor(itemView.context, R.attr.base_green),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            val isExpired =
                task.deadlineDate != null && task.deadlineDate!! < curDate
            isCompleteChBox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                getThemeAttrColor(
                    itemView.context,
                    if (isExpired) R.attr.color_delete_active else R.attr.support_separator_color
                ), PorterDuff.Mode.SRC_IN
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpTextViews(task: TodoItem) {
        taskTextTV.text =
            (if (task.priority == Priority.low) "⬇️" else "") +
                    (if (task.priority == Priority.important) "‼️" else "") + task.text
        deadlineDataTV.text =
            if (task.deadlineDate != null) converter.fromTimeStampToDate(task.deadlineDate!!)
                .toString() else ""
    }

    private fun setUpCheckbox(task: ToDoItemUIState) {
        isCompleteChBox.isChecked = task.todoItem.isCompleted
        isCompleteChBox.setOnClickListener {
            task.onCheck()
        }
    }
}