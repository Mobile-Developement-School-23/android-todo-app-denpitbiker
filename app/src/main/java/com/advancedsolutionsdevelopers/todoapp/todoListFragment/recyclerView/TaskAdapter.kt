package com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.Priority
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import java.time.LocalDate


class TaskAdapter(
    private val navController: NavController,
    private val viewModel: TasksListViewModel
) : RecyclerView.Adapter<TaskViewHolder>() {
    var tasks = arrayListOf<TodoItem>()
    var isCheckedChange: Boolean = false
    private var alertDialog: AlertDialog? = null
    private val curDate: LocalDate = LocalDate.now()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(viewType, parent, false))
    }

    override fun getItemCount() =
        tasks.size + 1//Потому, что последний элемент - кнопка создания новой задачи

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (position == tasks.size) {
            holder.itemView.setOnClickListener {
                navController.navigate(R.id.action_todoListFragment_to_taskFragment)
            }
            return
        }
        val task = tasks[position]
        setUpCheckbox(holder, task)
        setUpTextViews(holder, task)
        setItemClickListeners(holder.itemView, task)
        holder.onBind(task, curDate)
    }

    private fun setUpTextViews(holder: TaskViewHolder, task: TodoItem) {
        holder.taskTextTextview.text =
            (if (task.priority == Priority.Low) "⬇️" else "") +
                    (if (task.priority == Priority.High) "‼️" else "") + task.text
        holder.deadlineDataTextview.text =
            if (task.deadlineDate != null) task.deadlineDate.toString() else ""
    }

    private fun setUpCheckbox(holder: TaskViewHolder, task: TodoItem) {
        holder.isCompleteCheckbox.isChecked = task.isCompleted
        holder.isCompleteCheckbox.setOnClickListener { _ ->
            val data: ArrayList<TodoItem> = viewModel.tasks.value!!
            for (i in 0 until data.size) {
                if (data[i].id == task.id) {
                    data[i].isCompleted = !data[i].isCompleted
                    holder.onBind(data[i], curDate)
                    isCheckedChange = true
                    break
                }
            }
            viewModel.tasks.value = data
        }
    }

    private fun setItemClickListeners(iv: View, task: TodoItem) {
        iv.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("taskId", task.id)
            navController.navigate(R.id.action_todoListFragment_to_taskFragment, bundle)
        }
        iv.setOnLongClickListener {
            if (alertDialog == null) {
                alertDialog = buildAlertDialog(iv)
            }
            alertDialog!!.show()
            true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == tasks.size) R.layout.rv_end_button else R.layout.rv_item
    }
    //Позже добавлю полезный функционал сюда
    private fun buildAlertDialog(view: View): AlertDialog {
        val items = arrayOf<CharSequence>(view.context.getString(R.string.toast_2_u))
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            ContextThemeWrapper(view.context, R.style.AlertDialogCustom)
        )
        builder.setTitle(R.string.actions)
        builder.setItems(items) { _, _ ->
            Toast.makeText(view.context, R.string.goofy_ahh_string, Toast.LENGTH_LONG).show()
        }
        return builder.create()
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