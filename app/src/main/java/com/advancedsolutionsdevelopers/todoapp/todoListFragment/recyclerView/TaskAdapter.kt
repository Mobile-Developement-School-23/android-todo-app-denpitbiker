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
import com.advancedsolutionsdevelopers.todoapp.data.Constant.task_id_key
import com.advancedsolutionsdevelopers.todoapp.data.Converters
import com.advancedsolutionsdevelopers.todoapp.data.Priority
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class TaskAdapter(private val navController: NavController,private val fragmentScope: CoroutineScope) :
    RecyclerView.Adapter<TaskViewHolder>() {
    var tasks = arrayListOf<TodoItem>()
    private var alertDialog: AlertDialog? = null
    private val converter = Converters()
    private val curDate by lazy { converter.dateToTimestamp(LocalDate.now(converter.zoneId)) }

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
            (if (task.priority == Priority.low) "⬇️" else "") +
                    (if (task.priority == Priority.important) "‼️" else "") + task.text
        holder.deadlineDataTextview.text =
            if (task.deadlineDate != null) converter.fromTimeStampToDate(task.deadlineDate!!)
                .toString() else ""
    }

    private fun setUpCheckbox(holder: TaskViewHolder, task: TodoItem) {
        holder.isCompleteCheckbox.isChecked = task.isCompleted
        holder.isCompleteCheckbox.setOnClickListener {
            fragmentScope.launch(Dispatchers.IO) {
                val data = TodoItemsRepository.getTaskById(task.id)
                data.isCompleted = !data.isCompleted
                TodoItemsRepository.updateTask(data)
            }
        }
    }

    private fun setItemClickListeners(iv: View, task: TodoItem) {
        iv.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(task_id_key, task.id)
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