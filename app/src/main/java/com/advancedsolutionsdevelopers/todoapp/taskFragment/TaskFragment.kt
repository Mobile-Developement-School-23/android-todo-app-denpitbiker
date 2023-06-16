package com.advancedsolutionsdevelopers.todoapp.taskFragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.Priority
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.google.android.material.appbar.AppBarLayout
import java.time.LocalDate


class TaskFragment : Fragment() {
    private var taskId: String = ""
    private var taskIndex: Int = -1
    private var priority: Int = 0
    private val curDate = LocalDate.now()
    private var isCalendarWasOpen = false
    private var isEditMode = false
    private var deadlineDate = LocalDate.of(2000, 1, 1)
    private var datePickerDialog: DatePickerDialog? = null
    private lateinit var viewModel: TasksListViewModel
    private lateinit var data: ArrayList<TodoItem>
    private lateinit var priorities: Array<String>
    private lateinit var priorityTextView: TextView
    private lateinit var closeButton: ImageButton
    private lateinit var saveButton: TextView
    private lateinit var deleteButton: LinearLayout
    private lateinit var taskEditText: EditText

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var doUntilSwitch: Switch
    private lateinit var doUntilButton: TextView
    private lateinit var taskScrollView: ScrollView
    private lateinit var taskAppBar: AppBarLayout
    private lateinit var deleteTextView: TextView
    private lateinit var deleteImageView: ImageView
    private lateinit var priorityPopupMenuButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TasksListViewModel::class.java]
        data = viewModel.tasks.value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        priorities = resources.getStringArray(R.array.priority)
        findViews(view)
        setupListeners()
        datePickerDialog = generateCalendarDialog(view)
        if (arguments != null) {
            taskId = requireArguments().getString("taskId", "")
            taskIndex = findTaskIndexById(taskId)
            isEditMode = true
        }
        setupDeleteButton(view)
        setupSaveButton(view)
        fillViewsOnMode()
    }

    //Заполняем views в зависимости от того, в каком режиме открыт фрагмент:
    //редактирование существующей записи/создание новой
    private fun fillViewsOnMode() {
        if (isEditMode) {
            taskEditText.setText(data[taskIndex].text)
            priorityTextView.text = priorities[data[taskIndex].priority.ordinal]
            doUntilSwitch.isChecked = data[taskIndex].deadlineDate != null
            doUntilButton.text =
                if (data[taskIndex].deadlineDate != null) data[taskIndex].deadlineDate.toString() else curDate.toString()
        } else {
            priorityTextView.text = priorities[priority]
            doUntilButton.text = curDate.toString()
        }
    }

    private fun generateCalendarDialog(view: View): DatePickerDialog {
        val datePickerListener = OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            doUntilButton.text = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            deadlineDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            isCalendarWasOpen = true
        }
        return DatePickerDialog(
            view.context,
            datePickerListener,
            curDate.year,
            curDate.monthValue - 1,
            curDate.dayOfMonth
        )
    }

    private fun findViews(parent: View) {
        closeButton = parent.findViewById(R.id.close_button)
        deleteButton = parent.findViewById(R.id.delete_button)
        deleteImageView = parent.findViewById(R.id.delete_imageview)
        deleteTextView = parent.findViewById(R.id.delete_textview)
        doUntilButton = parent.findViewById(R.id.calendar_button)
        doUntilSwitch = parent.findViewById(R.id.do_until_switch)
        priorityPopupMenuButton = parent.findViewById(R.id.priority_popup_menu_button)
        priorityTextView = parent.findViewById(R.id.priority_textview)
        saveButton = parent.findViewById(R.id.save_button)
        taskAppBar = parent.findViewById(R.id.task_app_bar)
        taskEditText = parent.findViewById(R.id.task_description_edittext)
        taskScrollView = parent.findViewById(R.id.task_scroll_view)
    }

    private fun setupListeners() {
        taskScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                taskAppBar.elevation = 10f
            } else {
                taskAppBar.elevation = 0f
            }
        }
        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        doUntilSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                doUntilButton.visibility = View.VISIBLE
                doUntilButton.isClickable = true
            } else {
                doUntilButton.visibility = View.INVISIBLE
                doUntilButton.isClickable = false
            }
        }
        doUntilButton.setOnClickListener {
            datePickerDialog?.show()
        }
        priorityPopupMenuButton.setOnClickListener {
            openPopupMenu(it)
        }
    }

    private fun setupSaveButton(view: View) {
        saveButton.setOnClickListener {
            if (taskEditText.text.toString() == "") {
                Toast.makeText(view.context, R.string.enter_something_first, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (taskIndex == -1) {
                val x = TodoItem(
                    if (data.size > 0) (data[data.size - 1].id.toLong() + 1).toString() else "0",//Да, так нельзя!!!
                    taskEditText.text.toString(),
                    Priority.values()[priority],
                    false,
                    curDate,
                    if (doUntilSwitch.isChecked) (if (isCalendarWasOpen) deadlineDate else curDate) else null,
                    curDate
                )
                data.add(x)
            } else {
                val x = TodoItem(
                    taskId,
                    taskEditText.text.toString(),
                    Priority.values()[priority],
                    data[taskIndex].isCompleted,
                    data[taskIndex].creationDate,
                    if (doUntilSwitch.isChecked) (if (isCalendarWasOpen) deadlineDate else
                            (if (data[taskIndex].deadlineDate != null) data[taskIndex].deadlineDate else curDate)) else null,
                    curDate
                )
                data[taskIndex] = x
            }
            closeButton.callOnClick()
        }
    }

    private fun setupDeleteButton(view: View) {
        deleteButton.isClickable = isEditMode
        if (isEditMode) {
            deleteButton.setOnClickListener {
                Toast.makeText(
                    view.context, R.string.deleted, Toast.LENGTH_SHORT
                ).show()
                data.removeAt(taskIndex)
                closeButton.callOnClick()
            }
        }
        val color = HandyFunctions.getThemeAttrColor(
            view.context,
            if (isEditMode) R.attr.color_delete_active else R.attr.color_delete_inactive
        )
        deleteTextView.setTextColor(color)
        deleteImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    private fun openPopupMenu(v: View) {
        showPopupMenu(v)
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(v.context, R.style.Priority_PopupMenu), v)
        popupMenu.inflate(R.menu.popup_priority_menu)
        popupMenu.setOnMenuItemClickListener {
            priority = it.order
            when (it.itemId) {
                R.id.standard_priority_button, R.id.low_priority_button, R.id.high_priority_button -> {
                    priorityTextView.text = priorities[priority]
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    priority = 0
                    return@setOnMenuItemClickListener false
                }
            }
        }
        val itemRecall = SpannableString(priorities[2])
        itemRecall.setSpan(
            ForegroundColorSpan(
                HandyFunctions.getThemeAttrColor(
                    requireView().context, R.attr.color_delete_active
                )
            ), 0, itemRecall.length, 0
        )
        popupMenu.menu.removeItem(R.id.high_priority_button)
        popupMenu.menu.add(Menu.NONE, R.id.high_priority_button, 2, itemRecall)
        popupMenu.show()
    }

    private fun findTaskIndexById(id: String): Int {
        //Да, не красиво, но бд-то нет
        for (i in 0 until data.size) {
            if (data[i].id == id) {
                return i
            }
        }
        return 0
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.tasks.value = data
    }
}