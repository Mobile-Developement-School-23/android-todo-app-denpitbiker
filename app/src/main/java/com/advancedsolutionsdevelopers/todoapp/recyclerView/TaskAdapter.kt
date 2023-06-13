package com.advancedsolutionsdevelopers.todoapp.recyclerView

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.HandyFunctions
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.TasksListViewModel
import java.time.LocalDate


class TaskAdapter(
    private val navController: NavController,
    private val viewModel: TasksListViewModel
) : RecyclerView.Adapter<TaskViewHolder>() {
    var tasks = arrayListOf<TodoItem>()
    private val curDate: LocalDate = LocalDate.now()
    var isCheckedChange: Boolean = false
    var isVisibilityChanges: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.rv_item, parent, false))
    }

    override fun getItemCount() = tasks.size
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.isCompleteCheckbox.isChecked = task.isCompleted
        holder.isCompleteCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (!isVisibilityChanges) {
                val data: ArrayList<TodoItem> = viewModel.tasks.value!!
                for (i in 0 until data.size) {
                    if (data[i].id == task.id) {
                        data[i].isCompleted = isChecked
                        updateUI(data[i], holder)
                        isCheckedChange = true
                        break
                    }
                }
                viewModel.tasks.value = data
            }
        }
        holder.taskTextTextview.text = task.text
        if (task.deadlineDate != null)
            holder.deadlineDataTextview.text = task.deadlineDate.toString()
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("taskId", task.id)
            navController.navigate(R.id.action_todoListFragment_to_taskFragment, bundle)
        }
        updateUI(task, holder)
    }

    private fun updateUI(task: TodoItem, holder: TaskViewHolder) {
        if (task.isCompleted) {
            holder.isCompleteCheckbox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                HandyFunctions.getThemeAttrColor(
                    holder.itemView.context,
                    R.attr.base_green
                ), PorterDuff.Mode.SRC_IN
            )
            holder.taskTextTextview.paintFlags =
                holder.taskTextTextview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.taskTextTextview.setTextColor(
                HandyFunctions.getThemeAttrColor(
                    holder.itemView.context,
                    R.attr.support_separator_color
                )
            )
        } else {
            holder.taskTextTextview.paintFlags =
                holder.taskTextTextview.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.taskTextTextview.setTextColor(
                HandyFunctions.getThemeAttrColor(
                    holder.itemView.context,
                    R.attr.label_primary
                )
            )
            if (task.deadlineDate != null && task.deadlineDate!! > curDate) {
                holder.isCompleteCheckbox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                    HandyFunctions.getThemeAttrColor(
                        holder.itemView.context,
                        R.attr.color_delete_active
                    ), PorterDuff.Mode.SRC_IN
                )
            } else {
                holder.isCompleteCheckbox.buttonDrawable?.colorFilter = PorterDuffColorFilter(
                    HandyFunctions.getThemeAttrColor(
                        holder.itemView.context,
                        R.attr.support_separator_color
                    ), PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }
    fun insertData(insertList: List<TodoItem>) {
        val diffUtilCallback = TasksDiffUtilCallback(tasks, insertList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        tasks.addAll(insertList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateList(newList: List<TodoItem>) {
        val diffUtilCallback = TasksDiffUtilCallback(tasks, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        tasks.clear()
        tasks.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}