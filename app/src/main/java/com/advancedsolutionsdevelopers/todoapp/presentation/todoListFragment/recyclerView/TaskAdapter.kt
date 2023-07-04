package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.Priority
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.utils.Converters
import java.time.LocalDate


class TaskAdapter :
    ListAdapter<ToDoItemUIState, TaskViewHolder>(TasksDiffUtilCallback()) {
    private val converter = Converters()
    private val curDate by lazy { converter.dateToTimestamp(LocalDate.now(converter.zoneId)) }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            currentList[position].onClick()
        }
        if (currentList[position].todoItem.id.isEmpty()) {
            return
        }
        val task = currentList[position].todoItem
        setUpCheckbox(holder, currentList[position])
        setUpTextViews(holder, task)
        holder.onBind(task, curDate)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpTextViews(holder: TaskViewHolder, task: TodoItem) {
        holder.taskTextTextview.text =
            (if (task.priority == Priority.low) "⬇️" else "") +
                    (if (task.priority == Priority.important) "‼️" else "") + task.text
        holder.deadlineDataTextview.text =
            if (task.deadlineDate != null) converter.fromTimeStampToDate(task.deadlineDate!!)
                .toString() else ""
    }

    private fun setUpCheckbox(holder: TaskViewHolder, task: ToDoItemUIState) {
        holder.isCompleteCheckbox.isChecked = task.todoItem.isCompleted
        holder.isCompleteCheckbox.setOnClickListener {
            task.onCheck()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].todoItem.id.isEmpty()) R.layout.rv_end_button else R.layout.rv_item
    }
}